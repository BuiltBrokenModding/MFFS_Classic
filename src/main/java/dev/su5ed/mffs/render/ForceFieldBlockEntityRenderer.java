package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class ForceFieldBlockEntityRenderer implements BlockEntityRenderer<ForceFieldBlockEntity> {

    public ForceFieldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ForceFieldBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState camouflage = blockEntity.getCamouflage();
        if (camouflage != null) {
            BlockEntityRenderDelegate.INSTANCE.tryRenderDelegate(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}
