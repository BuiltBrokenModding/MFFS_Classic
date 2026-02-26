package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.render.model.CoercionDeriverTopModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CoercionDeriverRenderer implements BlockEntityRenderer<FortronBlockEntity, CoercionDeriverRenderer.CoercionDeriverRenderState> {
    public static final Identifier COERCION_DERIVER_OFF_TEXTURE = MFFSMod.location("textures/model/coercion_deriver_off.png");
    public static final Identifier COERCION_DERIVER_ON_TEXTURE = MFFSMod.location("textures/model/coercion_deriver_on.png");

    private final ModelPart top;

    public CoercionDeriverRenderer(BlockEntityRendererProvider.Context context) {
        this.top = context.bakeLayer(CoercionDeriverTopModel.LAYER_LOCATION);
    }
    
    public static class CoercionDeriverRenderState extends BlockEntityRenderState {
        public boolean isActive;
        public int animation;
    }

    @Override
    public CoercionDeriverRenderState createRenderState() {
        return new CoercionDeriverRenderState();
    }

    @Override
    public void extractRenderState(FortronBlockEntity blockEntity, CoercionDeriverRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.isActive = blockEntity.isActive();
        renderState.animation = blockEntity.getAnimation();
    }

    @Override
    public void submit(CoercionDeriverRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        Identifier texture = renderState.isActive ? COERCION_DERIVER_ON_TEXTURE : COERCION_DERIVER_OFF_TEXTURE;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.96, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        poseStack.scale(1.3f, 1.3f, 1.3f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.animation));

        nodeCollector.submitModelPart(this.top, poseStack, RenderTypes.entityTranslucent(texture), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }
}
