package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.render.model.ForceCubeModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ClientRenderHandler {
    public static void renderCubeMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        Vec3 centerPos = Vec3.atCenterOf(be.getBlockPos());
        ModelPart cubeModel = modelFactory.apply(ForceCubeModel.LAYER_LOCATION);
        RenderTickHandler.addTransparentRenderer(ForceCubeModel.RENDER_TYPE, new CubeModeRenderer(centerPos, cubeModel));
    }

    private record CubeModeRenderer(Vec3 centerPos, ModelPart cubeModel) implements LazyRenderer {
        @Override
        public void render(PoseStack poseStack, VertexConsumer buffer, int renderTick, float partialTick) {
            float ticks = renderTick + partialTick;

            float alpha = (float) (Math.sin(ticks / 10.0) / 2.0 + 1.0);
            float scale = 0.55f;

            poseStack.pushPose();
            poseStack.translate(this.centerPos.x, this.centerPos.y, this.centerPos.z);
            poseStack.translate(0, 1 + Math.sin(Math.toRadians(ticks * 3L)) / 7.0, 0);

            poseStack.scale(scale, scale, scale);

            poseStack.mulPose(Vector3f.YP.rotationDegrees(ticks * 4L));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(36 + ticks * 4L));

            this.cubeModel.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, Math.min(alpha, 1));

            poseStack.popPose();
        }

        @Nullable
        @Override
        public Vec3 getCenterPos(float partialTick) {
            return this.centerPos;
        }
    }

    private ClientRenderHandler() {}
}
