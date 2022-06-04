package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.AnimatedBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CoercionDeriverBlockRenderer implements BlockEntityRenderer<AnimatedBlockEntity> {
    public static final ResourceLocation COERCION_DERIVER_OFF_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/coercion_deriver_off.png");
    public static final ResourceLocation COERCION_DERIVER_ON_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/model/coercion_deriver_on.png");

    private final ModelPart top;

    public CoercionDeriverBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.top = context.bakeLayer(CoercionDeriverTopModel.LAYER_LOCATION);
    }

    @Override
    public void render(AnimatedBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ResourceLocation texture = blockEntity.isActive() ? COERCION_DERIVER_ON_TEXTURE : COERCION_DERIVER_OFF_TEXTURE;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.96, 0.5);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
        poseStack.scale(1.3f, 1.3f, 1.3f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(blockEntity.getAnimation()));

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        this.top.render(poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();

    }
}
