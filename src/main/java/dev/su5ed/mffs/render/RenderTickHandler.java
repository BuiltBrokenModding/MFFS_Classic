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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.TickEvent;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Source: Mekanism <a href="https://github.com/mekanism/Mekanism/blob/6093851f05dfb5ff2da52ace87f06ea03a7571a4/src/main/java/mekanism/client/render/RenderTickHandler.java">RenderTickHandler</a>
 */
@EventBusSubscriber(modid = MFFSMod.MODID, value = Dist.CLIENT)
public final class RenderTickHandler {
    private static final Map<RenderType, List<LazyRenderer>> transparentRenderers = new HashMap<>();

    public static void addTransparentRenderer(RenderType renderType, LazyRenderer render) {
        transparentRenderers.computeIfAbsent(renderType, r -> new ArrayList<>()).add(render);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            transparentRenderers.clear();
        }
    }

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Minecraft minecraft = Minecraft.getInstance();
            Camera camera = event.getCamera();
            PoseStack poseStack = event.getPoseStack();
            int ticks = event.getRenderTick();
            float partialTicks = event.getPartialTick();
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

            RenderPostProcessor.prepareRender();

            poseStack.pushPose();
            // here we translate based on the inverse position of the client viewing camera to get back to 0, 0, 0
            Vec3 camPos = camera.getPosition();
            poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

            // Render
            Consumer<TransparentRenderInfo> consumer = info -> info.render(poseStack, bufferSource, ticks, partialTicks);
            if (transparentRenderers.size() == 1) {
                //If we only have one render type we don't need to bother calculating any distances
                EntryStream.of(transparentRenderers)
                    .mapKeyValue(TransparentRenderInfo::new)
                    .forEach(consumer);
            } else {
                EntryStream.of(transparentRenderers)
                    .mapKeyValue((renderType, renderers) -> {
                        double closest = StreamEx.of(renderers)
                            .mapPartial(renderer -> Optional.ofNullable(renderer.centerPos()))
                            .mapToDouble(camPos::distanceToSqr)
                            .min()
                            .orElse(Double.MAX_VALUE);
                        //Note: we remap it in order to keep track of the closest distance so that we only have to calculate it once
                        return new TransparentRenderInfo(renderType, renderers, closest);
                    })
                    //Sort in the order of furthest to closest (reverse of by closest)
                    .reverseSorted(Comparator.comparingDouble(TransparentRenderInfo::closest))
                    .forEachOrdered(consumer);
            }
            transparentRenderers.clear();

            poseStack.popPose();
        }

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            RenderPostProcessor.process(event.getRenderTick());
        }
    }

    public record TransparentRenderInfo(RenderType renderType, List<LazyRenderer> renders, double closest) {
        public TransparentRenderInfo(RenderType renderType, List<LazyRenderer> renders) {
            this(renderType, renders, 0);
        }

        private void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, int ticks, float partialTicks) {
            //Batch all renders for a single render type into a single buffer addition
            VertexConsumer buffer = bufferSource.getBuffer(this.renderType);
            for (LazyRenderer renderer : this.renders) {
                //Note: We don't bother sorting renders in a specific render type as we assume the render type has sortOnUpload as true
                renderer.render(poseStack, buffer, ticks, partialTicks);
            }
            bufferSource.endBatch(this.renderType);
        }
    }

    private RenderTickHandler() {}
}
