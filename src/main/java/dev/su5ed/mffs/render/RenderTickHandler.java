/*
 * MIT License
 *
 * Copyright (c) 2017-2022 Aidan C. Brady
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.su5ed.mffs.render;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reference (1.20.1): uses {@code @EventBusSubscriber(Dist.CLIENT)},
 *   {@code RenderLevelStageEvent.AfterWeather}, {@code MultiBufferSource.BufferSource}.
 * In 1.12.2: uses {@code @Mod.EventBusSubscriber(Side.CLIENT)},
 *   {@code RenderWorldLastEvent}, direct {@code GlStateManager} for GL state.
 *
 * Source inspiration: Mekanism
 * <a href="https://github.com/mekanism/Mekanism/blob/6093851f05dfb5ff2da52ace87f06ea03a7571a4/src/main/java/mekanism/client/render/RenderTickHandler.java">RenderTickHandler</a>
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MFFSMod.MODID)
public final class RenderTickHandler {
    private static final Map<ModRenderType, List<LazyRenderer>> transparentRenderers = new HashMap<>();
    /** Monotonic client tick counter; incremented on each CLIENT tick START. */
    private static int clientTicks = 0;

    /**
     * Enqueue a {@link LazyRenderer} to be rendered on the next {@link RenderWorldLastEvent}.
     * Should be called from a TESR's {@code render()} method.
     * The queue is cleared automatically at the start of each client tick.
     */
    public static void addTransparentRenderer(ModRenderType renderType, LazyRenderer render) {
        transparentRenderers.computeIfAbsent(renderType, r -> new ArrayList<>()).add(render);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            transparentRenderers.clear();
            clientTicks++;
        }
    }

    @SubscribeEvent
    public static void renderLevelLate(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewer = mc.getRenderViewEntity();
        if (viewer == null) return;

        float partialTick = event.getPartialTicks();
        double camX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTick;
        double camY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTick;
        double camZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTick;

        // Render camo-TESR delegates (force field blocks camouflaged as chests etc.).
        // This replaces the old class-wide ForceFieldBlockEntityRenderer TESR registration;
        // only the small set of instances that actually have a delegate is iterated here.
        BlockEntityRenderDelegate.INSTANCE.renderAllDelegates(camX, camY, camZ, partialTick);

        if (transparentRenderers.isEmpty()) return;

        final Vec3d camPos = new Vec3d(camX, camY, camZ);
        final int ticks = clientTicks;

        // Translate to world-origin (blocks at absolute coords) from camera-relative space
        GlStateManager.pushMatrix();
        GlStateManager.translate(-camX, -camY, -camZ);

        if (transparentRenderers.size() == 1) {
            // Simple path: no distance sorting needed
            transparentRenderers.forEach((type, renderers) ->
                renderGroup(type, renderers, ticks, partialTick));
        } else {
            // Sort render type groups back-to-front (furthest first) for correct alpha blending
            EntryStream.of(transparentRenderers)
                .mapKeyValue((type, renderers) -> {
                    double closest = StreamEx.of(renderers)
                        .mapPartial(r -> Optional.ofNullable(r.centerPos()))
                        .mapToDouble(pos -> pos.squareDistanceTo(camPos))
                        .min()
                        .orElse(Double.MAX_VALUE);
                    return new TransparentRenderInfo(type, renderers, closest);
                })
                // Reverse sort: largest distance first
                .reverseSorted(Comparator.comparingDouble(info -> info.closest))
                .forEach(info -> renderGroup(info.renderType, info.renderers, ticks, partialTick));
        }

        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        transparentRenderers.clear();
    }

    private static void renderGroup(ModRenderType renderType, List<LazyRenderer> renderers, int ticks, float partialTick) {
        renderType.setup();
        for (LazyRenderer renderer : renderers) {
            renderer.render(ticks, partialTick);
        }
        renderType.teardown();
    }

    // Simple data carrier for distance-sorted rendering
    private static final class TransparentRenderInfo {
        final ModRenderType renderType;
        final List<LazyRenderer> renderers;
        final double closest;

        TransparentRenderInfo(ModRenderType renderType, List<LazyRenderer> renderers, double closest) {
            this.renderType = renderType;
            this.renderers = renderers;
            this.closest = closest;
        }
    }

    private RenderTickHandler() {}
}
