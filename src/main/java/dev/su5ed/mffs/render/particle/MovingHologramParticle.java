package dev.su5ed.mffs.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.util.TranslucentVertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MovingHologramParticle extends Particle {

    public MovingHologramParticle(ClientLevel level, Vec3 pos, ParticleColor color, int lifetime) {
        super(level, pos.x(), pos.y(), pos.z(), 0, 0, 0);

        setColor(color.getRed(), color.getGreen(), color.getBlue());
        setLifetime(lifetime);
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        PoseStack pose = new PoseStack();
        pose.pushPose();

        Vec3 vec3 = renderInfo.getPosition();
        float xx = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float yy = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float zz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        pose.translate(xx, yy, zz);

        pose.translate(0.5, 0.5, 0.5);
        pose.scale(1.01f, 1.01f, 1.01f);
        pose.translate(-0.5, -0.5, -0.5);

        float completion = this.age / (float) this.lifetime;
        pose.scale(1, completion, 1);

        float op = 0.5f;

        int remaining = this.lifetime - this.age;
        if (remaining <= 4) {
            op = 0.5f - (5 - remaining) * 0.1F;
        }

        int alpha = (int) (255 * op * 2);
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();
        BlockState state = ModBlocks.FORCE_FIELD.get().defaultBlockState();
        BlockStateModel model = blockRenderer.getBlockModel(state);
        modelRenderer.renderModel(
            pose.last(),
            t -> new TranslucentVertexConsumer(buffer, alpha),
            model,
            this.rCol, this.gCol, this.bCol,
            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
            this.level, new BlockPos((int) getPos().x, (int) getPos().y, (int) getPos().z), state
        );

        pose.popPose();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderType.HOLO;
    }
}
