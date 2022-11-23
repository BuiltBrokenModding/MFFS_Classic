package dev.su5ed.mffs.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.render.TranslucentVertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelData;

public final class ClientRenderHandler {
    public static final ResourceLocation FORCE_CUBE_MODEL = new ResourceLocation(MFFSMod.MODID, "block/force_cube");

    public static void renderCubeMode(PoseStack poseStack, MultiBufferSource bufferSource, long ticks) {
        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(FORCE_CUBE_MODEL);
        int opacity = (int) ((Math.sin(ticks / 10.0) / 2.0 + 1.0) * 255);
        float scale = 0.55f;

        poseStack.pushPose();

        poseStack.translate(0, 1 + Math.sin(Math.toRadians(ticks * 3L)) / 7.0, 0);

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5, -0.5, -0.5);

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(new Vector3f(0.0f, 1.0F, 0.0f).rotationDegrees(36 + ticks * 4L));
        poseStack.mulPose(new Vector3f(0.0f, 0.0F, 1.0f).rotationDegrees(36 + ticks * 4L));
        poseStack.translate(-0.5, -0.5, -0.5);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());
        VertexConsumer wrapped = new TranslucentVertexConsumer(buffer, Math.min(255, opacity));

        renderer.renderModel(
            poseStack.last(),
            wrapped,
            null,
            model,
            1F, 1F, 1F,
            LightTexture.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY,
            ModelData.EMPTY,
            RenderType.translucent()
        );

        poseStack.popPose();
    }

    private ClientRenderHandler() {}
}
