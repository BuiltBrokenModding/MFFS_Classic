package dev.su5ed.mffs.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.render.model.ForceCubeModel;
import dev.su5ed.mffs.render.model.ForceTubeModel;
import dev.su5ed.mffs.util.TranslucentVertexConsumer;
import dev.su5ed.mffs.util.projector.CylinderProjectorMode;
import dev.su5ed.mffs.util.projector.ModProjectorModes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ProjectorModeRenderer {
    public static final RenderType PYRAMID_RENDER_TYPE = ModRenderType.POS_TEX_TRANSLUCENT_UNCULLED_TRIANGLE.apply(ForceCubeModel.CORE_TEXTURE);
    private static final List<ProjectorMode> MODES = List.of(ModProjectorModes.CUBE, ModProjectorModes.SPHERE, ModProjectorModes.TUBE, ModProjectorModes.PYRAMID);
    private static final Map<ProjectorMode, RendererInfo> RENDERERS = Map.of(
        ModProjectorModes.CUBE, new RendererInfo(ForceCubeModel.LAYER_LOCATION, ForceCubeModel.RENDER_TYPE, CubeModeRenderer::new),
        ModProjectorModes.SPHERE, new RendererInfo(ForceCubeModel.LAYER_LOCATION, ForceCubeModel.RENDER_TYPE, SphereModeRenderer::new),
        ModProjectorModes.TUBE, new RendererInfo(ForceTubeModel.LAYER_LOCATION, ForceTubeModel.RENDER_TYPE, CubeModeRenderer::new),
        ModProjectorModes.PYRAMID, new RendererInfo(null, PYRAMID_RENDER_TYPE, (centerPos, model) -> new PyramidModeRenderer(centerPos))
    );

    private record RendererInfo(ModelLayerLocation model, RenderType renderType, BiFunction<Vec3, ModelPart, LazyRenderer> factory) {
        public LazyRenderer createRenderer(Vec3 centerPos, Function<ModelLayerLocation, ModelPart> modelFactory) {
            ModelPart modelPart = this.model != null ? modelFactory.apply(this.model) : null;
            return this.factory.apply(centerPos, modelPart);
        }
    }

    public static void renderCubeMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        addRenderer(be, modelFactory, ModProjectorModes.CUBE);
    }

    public static void renderSphereMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        addRenderer(be, modelFactory, ModProjectorModes.SPHERE);
    }

    public static void renderTubeMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        addRenderer(be, modelFactory, ModProjectorModes.TUBE);
    }

    public static void renderPyramidMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        addRenderer(be, modelFactory, ModProjectorModes.PYRAMID);
    }

    public static void renderCylinderMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        Vec3 centerPos = Vec3.atCenterOf(be.getBlockPos());
        ModelPart tubeModel = modelFactory.apply(ForceCubeModel.LAYER_LOCATION);
        RenderTickHandler.addTransparentRenderer(ForceCubeModel.RENDER_TYPE, new CylinderModeRenderer(centerPos, tubeModel));
    }

    public static void renderCustomMode(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        Vec3 centerPos = Vec3.atCenterOf(be.getBlockPos());
        RenderTickHandler.addTransparentRenderer(ForceCubeModel.RENDER_TYPE, new CustomModeRenderer(centerPos, modelFactory));
    }

    private static void addRenderer(BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory, ProjectorMode mode) {
        Vec3 centerPos = Vec3.atCenterOf(be.getBlockPos());
        RendererInfo info = RENDERERS.get(mode);
        RenderTickHandler.addTransparentRenderer(info.renderType, info.createRenderer(centerPos, modelFactory));
    }

    private static void hoverObject(PoseStack poseStack, float ticks, float scale, Vec3 centerPos) {
        poseStack.translate(centerPos.x, centerPos.y, centerPos.z);
        poseStack.translate(0, 1 + Math.sin(Math.toRadians(ticks * 3L)) / 7.0, 0);

        poseStack.scale(scale, scale, scale);

        poseStack.mulPose(Axis.YP.rotationDegrees(ticks * 4L));
        poseStack.mulPose(Axis.ZP.rotationDegrees(36 + ticks * 4L));
    }

    private record CustomModeRenderer(Vec3 centerPos, Function<ModelLayerLocation, ModelPart> modelFactory) implements LazyRenderer {
        private static final int PERIOD = 40;
        private static final int MAX = MODES.size() * PERIOD;

        @Override
        public void render(PoseStack poseStack, VertexConsumer buffer, int renderTick, float partialTick) {
            float ticks = renderTick + partialTick;
            MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
            int index = (int) ((ticks % MAX) / (double) PERIOD);
            ProjectorMode mode = MODES.get(index);
            RendererInfo info = RENDERERS.get(mode);
            LazyRenderer renderer = info.createRenderer(this.centerPos, this.modelFactory);
            float alpha = (float) -Math.pow(Math.sin((ticks + PERIOD / 2.0) * (Math.PI / (double) PERIOD)), 20) + 1;
            VertexConsumer actualConsumer = source.getBuffer(info.renderType);
            VertexConsumer wrapped = new TranslucentVertexConsumer(actualConsumer, (int) (alpha * 255));
            RenderSystem.setShaderColor(1, 1, 1, alpha);
            renderer.render(poseStack, wrapped, renderTick, partialTick);
            source.getBuffer(ForceCubeModel.RENDER_TYPE);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    private record CubeModeRenderer(Vec3 centerPos, ModelPart model) implements LazyRenderer {
        @Override
        public void render(PoseStack poseStack, VertexConsumer buffer, int renderTick, float partialTick) {
            float ticks = renderTick + partialTick;
            float alpha = (float) (Math.sin(ticks / 10.0) / 2.0 + 1.0);
            float scale = 0.55f;

            poseStack.pushPose();
            hoverObject(poseStack, ticks, scale, this.centerPos);
            this.model.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, Math.min(alpha, 1));
            poseStack.popPose();
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
            hoverObject(poseStack, ticks, scale, this.centerPos);
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
            hoverObject(poseStack, ticks, 1, this.centerPos);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

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
    }

    private record CylinderModeRenderer(Vec3 centerPos, ModelPart model) implements LazyRenderer {
        @Override
        public void render(PoseStack poseStack, VertexConsumer buffer, int renderTick, float partialTick) {
            float ticks = renderTick + partialTick;
            float scale = 0.15f;
            float radius = 1.5f;
            float detail = 0.5f;
            float alpha = (float) (Math.sin(ticks / 10.0) / 2.0 + 1.0);

            poseStack.pushPose();
            hoverObject(poseStack, ticks, scale, this.centerPos);
            int i = 0;
            for (float renderX = -radius; renderX <= radius; renderX += detail) {
                for (float renderZ = -radius; renderZ <= radius; renderZ += detail) {
                    for (float renderY = -radius; renderY <= radius; renderY += detail) {
                        float area = renderX * renderX + renderZ * renderZ + CylinderProjectorMode.RADIUS_EXPANSION;
                        if (area <= radius * radius && area >= (radius - 1) * (radius - 1) || (renderY == 0 || renderY == radius - 1) && area <= radius * radius) {
                            if (i % 2 == 0) {
                                Vec3 vector = new Vec3(renderX, renderY, renderZ);
                                poseStack.translate(vector.x, vector.y, vector.z);
                                this.model.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, Math.min(alpha, 1));
                                poseStack.translate(-vector.x, -vector.y, -vector.z);
                            }
                            i++;
                        }
                    }
                }
            }
            poseStack.popPose();
        }
    }

    private ProjectorModeRenderer() {}
}
