package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.item.CustomProjectorModeItem;
import dev.su5ed.mffs.network.StructureDataRequestPacket;
import dev.su5ed.mffs.setup.ModDataComponentTypes;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import one.util.streamex.StreamEx;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = MFFSMod.MODID, value = Dist.CLIENT)
public final class CustomProjectorModeClientHandler {
    private static final float MIN_ALPHA = 0.1F;
    private static final float MAX_ALPHA = BlockHighlighter.LIGHT_RED.alpha();
    private static final int PERIOD_TICKS = 30;
    private static final Map<ResourceKey<Level>, Map<String, VoxelShape>> STRUCTURE_SHAPES = new HashMap<>();

    public static VoxelShape getOrRequestShape(ItemStack stack, Level level) {
        String id = stack.get(ModDataComponentTypes.PATTERN_ID);
        if (id == null) {
            return null;
        }
        ResourceKey<Level> key = level.dimension();
        Map<String, VoxelShape> map = STRUCTURE_SHAPES.get(key);
        if (map == null || !map.containsKey(id)) {
            PacketDistributor.sendToServer(new StructureDataRequestPacket(id));
            setShape(key, id, null);
            return null;
        }
        return map.get(id);
    }

    public static void setShape(ResourceKey<Level> level, String id, VoxelShape shape) {
        Map<String, VoxelShape> map = STRUCTURE_SHAPES.computeIfAbsent(level, l -> new HashMap<>());
        map.put(id, shape);
    }

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            Minecraft minecraft = Minecraft.getInstance();
            StreamEx.of(minecraft.player.getMainHandItem(), minecraft.player.getOffhandItem())
                .findFirst(stack -> stack.is(ModItems.CUSTOM_MODE.get()))
                .ifPresent(stack -> {
                    PoseStack pose = new PoseStack();
                    Vec3 cameraPos = event.getCamera().getPosition();
                    CustomProjectorModeItem.StructureCoords coords = stack.get(ModDataComponentTypes.STRUCTURE_COORDS);
                    if (coords != null) {
                        BlockPos primary = coords.primary();
                        if (primary != null) {
                            BlockHighlighter.highlightBlock(pose, cameraPos, primary, BlockHighlighter.LIGHT_GREEN);

                            if (coords.secondary() != null) {
                                BlockPos secondary = coords.secondary();
                                BlockHighlighter.highlightBlock(pose, cameraPos, secondary, BlockHighlighter.LIGHT_RED);
                                BlockHighlighter.highlightArea(pose, cameraPos, primary, secondary);
                            } else if (minecraft.hitResult instanceof BlockHitResult blockHitResult) {
                                BlockPos secondary = blockHitResult.getBlockPos();
                                float alpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * 0.5F * Mth.abs(Mth.sin(2 * Mth.PI * (1 / (float) PERIOD_TICKS) * event.getRenderTick()) + 1.0F);
                                BlockHighlighter.highlightBlock(pose, cameraPos, secondary, BlockHighlighter.LIGHT_RED.withAlpha(alpha));
                                BlockHighlighter.highlightArea(pose, cameraPos, primary, secondary);
                            }
                        }
                    }
                    if (stack.has(ModDataComponentTypes.PATTERN_ID)) {
                        VoxelShape shape = getOrRequestShape(stack, minecraft.level);
                        if (shape != null) {
                            BlockHighlighter.highlightArea(pose, cameraPos, shape, null);
                        }
                    }
                });
        }
    }

    private CustomProjectorModeClientHandler() {}
}
