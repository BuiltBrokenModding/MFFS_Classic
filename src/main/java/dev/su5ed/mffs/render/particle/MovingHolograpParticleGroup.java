package dev.su5ed.mffs.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.util.TranslucentVertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MovingHolograpParticleGroup extends ParticleGroup<MovingHologramParticle> {
    public MovingHolograpParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick) {
        MovingHologramParticleRenderState state = new MovingHologramParticleRenderState();

        for (MovingHologramParticle particle : this.particles) {
            MovingHologramParticleData data = particle.extract(partialTick);
            state.particles.add(data);
        }

        return state;
    }

    public record MovingHologramParticleData(ParticleColor color, int age, int lifetime, double x0, double y0, double z0) {
    }

    public static class MovingHologramParticleRenderState implements ParticleGroupRenderState {
        private final List<MovingHologramParticleData> particles = new ArrayList<>();

        @Override
        public void submit(SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

            BlockState state = ModBlocks.FORCE_FIELD.get().defaultBlockState();
            BlockStateModel model = blockRenderer.getBlockModel(state);

            for (MovingHologramParticleData particle : this.particles) {
                PoseStack pose = new PoseStack();
                pose.pushPose();

                Vec3 vec3 = cameraRenderState.pos;
                float xx = (float) (particle.x0 - vec3.x());
                float yy = (float) (particle.y0 - vec3.y());
                float zz = (float) (particle.z0 - vec3.z());
                pose.translate(xx, yy, zz);

                pose.translate(0.5, 0.5, 0.5);
                pose.scale(1.01f, 1.01f, 1.01f);
                pose.translate(-0.5, -0.5, -0.5);

                float completion = particle.age / (float) particle.lifetime;
                pose.scale(1, completion, 1);

                float op = 0.5f;

                int remaining = particle.lifetime - particle.age;
                if (remaining <= 4) {
                    op = 0.5f - (5 - remaining) * 0.1F;
                }

                int alpha = (int) (255 * op * 2);

                nodeCollector.submitCustomGeometry(pose, RenderTypes.translucentMovingBlock(), (poseStack, consumer) -> {
                    TranslucentVertexConsumer wrapped = new TranslucentVertexConsumer(consumer, alpha);
                    ModelBlockRenderer.renderModel(
                        poseStack.copy(),
                        wrapped,
                        model,
                        particle.color.getRed(),
                        particle.color.getGreen(),
                        particle.color.getBlue(),
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY
                    );
                });

                pose.popPose();
            }
        }

        @Override
        public void clear() {
            ParticleGroupRenderState.super.clear();
        }
    }
}
