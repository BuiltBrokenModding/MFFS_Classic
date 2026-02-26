package dev.su5ed.mffs.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.su5ed.mffs.render.ModRenderType;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BeamParticleGroup extends ParticleGroup<BeamParticle> {
    public BeamParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick) {
        BeamParticleRenderState state = new BeamParticleRenderState();

        for (BeamParticle particle : this.particles) {
            BeamParticleData data = particle.extract(camera, partialTick);
            state.particles.add(data);
        }

        return state;
    }

    public record BeamParticleData(ParticleColor color, Vector3f[] vectors, Matrix4f mat, float vOffset, float length, float size, float opacity) {}

    public static class BeamParticleRenderState implements ParticleGroupRenderState {
        private final List<BeamParticleData> particles = new ArrayList<>();

        @Override
        public void submit(SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
            for (BeamParticleData particle : this.particles) {
                for (int i = 0; i < 3; i++) {
                    float u0 = 0.0F;
                    float u1 = 1.0F;
                    final float opacity = particle.opacity;
                    int brightness = LightTexture.FULL_BRIGHT;
                    Vector3f[] vectors = particle.vectors;
                    Matrix4f mat = particle.mat;

                    float v0 = -1.0F + particle.vOffset + i / 3.0F;
                    float v1 = particle.length * particle.size + v0;

                    mat.rotate(Axis.YP.rotationDegrees(60));

                    PoseStack pose = new PoseStack();
                    pose.pushPose();
                    pose.mulPose(mat);

                    nodeCollector.submitCustomGeometry(pose, ModRenderType.BEAM_PARTICLE, (p, consumer) -> {
                        consumer.addVertex(p, vectors[0].x(), vectors[0].y(), vectors[0].z())
                            .setUv(u1, v1)
                            .setColor(particle.color.getRed(), particle.color.getGreen(), particle.color.getBlue(), opacity)
                            .setLight(brightness);
                        consumer.addVertex(p, vectors[1].x(), vectors[1].y(), vectors[1].z())
                            .setUv(u1, v0)
                            .setColor(particle.color.getRed(), particle.color.getGreen(), particle.color.getBlue(), opacity)
                            .setLight(brightness);
                        consumer.addVertex(p, vectors[2].x(), vectors[2].y(), vectors[2].z())
                            .setUv(u0, v0)
                            .setColor(particle.color.getRed(), particle.color.getGreen(), particle.color.getBlue(), opacity)
                            .setLight(brightness);
                        consumer.addVertex(p, vectors[3].x(), vectors[3].y(), vectors[3].z())
                            .setUv(u0, v1)
                            .setColor(particle.color.getRed(), particle.color.getGreen(), particle.color.getBlue(), opacity)
                            .setLight(brightness);
                    });
                }
            }
        }

        @Override
        public void clear() {
            this.particles.clear();
        }
    }
}
