package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ForceFieldBlockEntityRenderer implements BlockEntityRenderer<ForceFieldBlockEntity, ForceFieldBlockEntityRenderer.ForceFieldRenderState> {

    public ForceFieldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class ForceFieldRenderState extends BlockEntityRenderState {
        public ForceFieldBlockEntity blockEntity;
        public boolean hasCamouflage;
    }

    @Override
    public ForceFieldRenderState createRenderState() {
        return new ForceFieldRenderState();
    }

    @Override
    public void extractRenderState(ForceFieldBlockEntity blockEntity, ForceFieldRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.blockEntity = null;
        renderState.hasCamouflage = blockEntity.getCamouflage() != null;

        if (renderState.hasCamouflage) {
            BlockEntityRenderDelegate.INSTANCE.prepareRenderState(blockEntity, partialTick, cameraPosition, breakProgress);
        }
    }

    @Override
    public void submit(ForceFieldRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (!renderState.hasCamouflage) {
            return;
        }
        BlockEntityRenderDelegate.INSTANCE.tryRenderDelegate(renderState.blockEntity, poseStack, nodeCollector, cameraRenderState);
    }
}
