package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.render.model.ForceCubeModel;
import dev.su5ed.mffs.render.model.ForceTubeModel;
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

    public static void renderSphereMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        Vec3 centerPos = Vec3.atCenterOf(be.getBlockPos());
        ModelPart cubeModel = modelFactory.apply(ForceCubeModel.LAYER_LOCATION);
        RenderTickHandler.addTransparentRenderer(ForceCubeModel.RENDER_TYPE, new SphereModeRenderer(centerPos, cubeModel));
    }

    public static void renderTubeMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        Vec3 centerPos = Vec3.atCenterOf(be.getBlockPos());
        ModelPart tubeModel = modelFactory.apply(ForceTubeModel.LAYER_LOCATION);
        RenderTickHandler.addTransparentRenderer(ForceTubeModel.RENDER_TYPE, new CubeModeRenderer(centerPos, tubeModel));
    }

    public static void renderPyramidMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        Vec3 centerPos = Vec3.atCenterOf(be.getBlockPos());
        RenderTickHandler.addTransparentRenderer(ModRenderType.POS_TEX_TRANSLUCENT_UNCULLED_TRIANGLE.apply(ForceCubeModel.CORE_TEXTURE), new PyramidModeRenderer(centerPos));
    }

    private record CubeModeRenderer(Vec3 centerPos, ModelPart model) implements LazyRenderer {
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

            this.model.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, Math.min(alpha, 1));

            poseStack.popPose();
        }

        @Nullable
        @Override
        public Vec3 getCenterPos(float partialTick) {
            return this.centerPos;
        }
    }

    private record SphereModeRenderer(Vec3 centerPos, ModelPart model) implements LazyRenderer {
        @Override
        public void render(PoseStack poseStack, VertexConsumer buffer, int renderTick, float partialTick) {
            float ticks = renderTick + partialTick;
            float alpha = (float) (Math.sin(ticks / 10.0) / 2.0 + 1.0);
            float scale = 0.2f;
            float radius = 1.5f;
            int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / radius / 2));

            poseStack.pushPose();
            poseStack.translate(this.centerPos.x, this.centerPos.y, this.centerPos.z);
            poseStack.translate(0, 1 + Math.sin(Math.toRadians(ticks * 3L)) / 7.0, 0);

            poseStack.scale(scale, scale, scale);

            poseStack.mulPose(Vector3f.YP.rotationDegrees(ticks * 4L));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(36 + ticks * 4L));

            for (int phi_n = 0; phi_n < 2 * steps; phi_n++) {
                for (int theta_n = 1; theta_n < steps; theta_n++) {
                    double phi = Math.PI * 2 / steps * phi_n;
                    double theta = Math.PI / steps * theta_n;

                    Vec3 vec = new Vec3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)).multiply(radius, radius, radius);
                    poseStack.translate(vec.x, vec.y, vec.z);
                    this.model.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, Math.min(alpha, 1) / 5f);
                    poseStack.translate(-vec.x, -vec.y, -vec.z);
                }
            }

            poseStack.popPose();
        }

        @Nullable
        @Override
        public Vec3 getCenterPos(float partialTick) {
            return this.centerPos;
        }
    }

    private record PyramidModeRenderer(Vec3 centerPos) implements LazyRenderer {
        @Override
        public void render(PoseStack poseStack, VertexConsumer buffer, int renderTick, float partialTick) {
            float ticks = renderTick + partialTick;
            float height = 0.5f;
            float width = 0.3f;
            int uvMaxX = 2;
            int uvMaxY = 2;
            
            poseStack.pushPose();
            poseStack.translate(this.centerPos.x, this.centerPos.y, this.centerPos.z);
            poseStack.translate(0, 1 + Math.sin(Math.toRadians(ticks * 3L)) / 7.0, 0);

            poseStack.mulPose(Vector3f.YP.rotationDegrees(ticks * 4L));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(36 + ticks * 4L));

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));

            Vector3f translation = new Vector3f(0, -0.4F, 0);
            Matrix4f mat = poseStack.last().pose();
            
            buffer.vertex(mat, translation.x(), translation.y(), translation.z()).uv(0, 0).endVertex();
            buffer.vertex(mat, -width + translation.x(), height + translation.y(), -width + translation.z()).uv(-uvMaxX, -uvMaxY).endVertex();
            buffer.vertex(mat, -width + translation.x(), height + translation.y(), width + translation.z()).uv(-uvMaxX, uvMaxY).endVertex();
            
            buffer.vertex(mat, translation.x(), translation.y(), translation.z()).uv(0, 0).endVertex();
            buffer.vertex(mat, -width + translation.x(), height + translation.y(), width + translation.z()).uv(-uvMaxX, uvMaxY).endVertex();
            buffer.vertex(mat, width + translation.x(), height + translation.y(), width + translation.z()).uv(uvMaxX, uvMaxY).endVertex();
            
            buffer.vertex(mat, translation.x(), translation.y(), translation.z()).uv(0, 0).endVertex();
            buffer.vertex(mat, width + translation.x(), height + translation.y(), width + translation.z()).uv(uvMaxX, uvMaxY).endVertex();
            buffer.vertex(mat, width + translation.x(), height + translation.y(), -width + translation.z()).uv(uvMaxX, -uvMaxY).endVertex();
            
            buffer.vertex(mat, translation.x(), translation.y(), translation.z()).uv(0, 0).endVertex();
            buffer.vertex(mat, width + translation.x(), height + translation.y(), -width + translation.z()).uv(uvMaxX, -uvMaxY).endVertex();
            buffer.vertex(mat, -width + translation.x(), height + translation.y(), -width + translation.z()).uv(-uvMaxX, -uvMaxY).endVertex();
            
            buffer.vertex(mat, -width + translation.x(), height + translation.y(), -width + translation.z()).uv(-uvMaxX, -uvMaxY).endVertex();
            buffer.vertex(mat, -width + translation.x(), height + translation.y(), width + translation.z()).uv(-uvMaxX, uvMaxY).endVertex();
            buffer.vertex(mat, width + translation.x(), height + translation.y(), width + translation.z()).uv(uvMaxX, uvMaxY).endVertex();
            
            buffer.vertex(mat, width + translation.x(), height + translation.y(), width + translation.z()).uv(uvMaxX, uvMaxY).endVertex();
            buffer.vertex(mat, width + translation.x(), height + translation.y(), -width + translation.z()).uv(uvMaxX, -uvMaxY).endVertex();
            buffer.vertex(mat, -width + translation.x(), height + translation.y(), -width + translation.z()).uv(-uvMaxX, -uvMaxY).endVertex();

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
