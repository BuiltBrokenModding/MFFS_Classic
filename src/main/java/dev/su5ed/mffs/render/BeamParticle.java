package dev.su5ed.mffs.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

public class BeamParticle extends TextureSheetParticle {
    private static final ResourceLocation FORTRON_TEXTURE = new ResourceLocation(MFFSMod.MODID, "textures/particle/fortron.png");

    private Vec3 target;
    private float length;
    private float rotYaw;
    private float rotPitch;
    private float prevYaw;
    private float prevPitch;

    private float endModifier = 1.0F;
    private boolean reverse = false;
    private boolean pulse = true;
    private int rotationSpeed = 20;
    private float prevSize;

    public BeamParticle(ClientLevel pLevel, SpriteSet pSpriteSet, Vec3 start, Vec3 target, int lifetime) {
        super(pLevel, start.x, start.y, start.z, 0, 0, 0);

        setSpriteFromAge(pSpriteSet);
        setColor(0.6f, 0.6f, 1f);
        setSize(0.02f, 0.02f);

        this.target = target;
        this.xd = this.xo - this.target.x;
        this.yd = this.yo - this.target.y;
        this.zd = this.zo - this.target.z;
        this.length = (float) start.distanceTo(this.target);
        this.rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
        double destX = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
        this.rotPitch = (float) (Math.atan2(yd, destX) * 180.0D / Math.PI);
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;
        
        setBoundingBox(new AABB(start, this.target));

        this.lifetime = lifetime;

        // TODO Sets the particle age based on distance.
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        float xd = (float) (this.x - this.target.x);
        float yd = (float) (this.y - this.target.y);
        float zd = (float) (this.z - this.target.z);

        this.length = Mth.sqrt(xd * xd + yd * yd + zd * zd);

        double destX = Math.sqrt(xd * xd + zd * zd);

        this.rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
        this.rotPitch = (float) (Math.atan2(yd, destX) * 180.0D / Math.PI);

        if (this.age++ >= this.lifetime) {
            remove();
        }
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();

        int deg = 360 / this.rotationSpeed;
        float rot = this.level.getDayTime() % deg * this.rotationSpeed + this.rotationSpeed * pPartialTicks;

        float size = 1.0f;
        if (this.pulse) {
            size = Math.min(this.age / 4.0F, 1.0F);
            size = Mth.lerp(pPartialTicks, this.prevSize, size);
        }

        float opacity = 0.5F;
        if (this.pulse && this.lifetime - this.age <= 4) {
            opacity = 0.5F - (4 - (this.lifetime - this.age)) * 0.1F;
        }

        float tickSlide = this.level.getGameTime() + pPartialTicks;
        if (this.reverse) {
            tickSlide *= -1.0F;
        }
        float vOffset = -tickSlide * 0.2F - Mth.floor(-tickSlide * 0.1F);

        Vec3 vec3 = pRenderInfo.getPosition();
        float xx = (float) (Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float yy = (float) (Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float zz = (float) (Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());
        mat.translate(new Vector3f(xx, yy, zz));

        float ry = Mth.lerp(pPartialTicks, this.prevYaw, this.rotYaw);
        float rp = Mth.lerp(pPartialTicks, this.prevPitch, this.rotPitch);
        mat.multiply(Vector3f.XP.rotationDegrees(90));
        mat.multiply(Vector3f.ZN.rotationDegrees(180 + ry));
        mat.multiply(Vector3f.XP.rotationDegrees(rp));

        float xNegMin = -0.15f * size;
        float xNegMax = -0.15f * size * this.endModifier;
        float xPosMin = 0.15f * size;
        float xPosMax = 0.15f * size * this.endModifier;
        float yMax = this.length * size;

        Vector3f[] vectors = new Vector3f[] {
            new Vector3f(xNegMax, yMax, 0.0F),
            new Vector3f(xNegMin, 0.0F, 0.0F),
            new Vector3f(xPosMin, 0.0F, 0.0F),
            new Vector3f(xPosMax, yMax, 0.0F)
        };
        
        float u0 = 0.0F;
        float u1 = 1.0F;
        int brightness = LightTexture.FULL_BRIGHT;

        mat.multiply(Vector3f.YP.rotationDegrees(rot));
        for (int i = 0; i < 3; i++) {
            float v0 = -1.0F + vOffset + i / 3.0F;
            float v1 = this.length * size + v0;
                    
            mat.multiply(Vector3f.YP.rotationDegrees(60));

            pBuffer.vertex(mat, vectors[0].x(), vectors[0].y(), vectors[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, opacity).uv2(brightness).endVertex();
            pBuffer.vertex(mat, vectors[1].x(), vectors[1].y(), vectors[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, opacity).uv2(brightness).endVertex();
            pBuffer.vertex(mat, vectors[2].x(), vectors[2].y(), vectors[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, opacity).uv2(brightness).endVertex();
            pBuffer.vertex(mat, vectors[3].x(), vectors[3].y(), vectors[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, opacity).uv2(brightness).endVertex();
        }

        this.prevSize = size;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return BeamParticleRenderType.INSTANCE;
    }

    public static class BeamParticleRenderType implements ParticleRenderType {
        public static final BeamParticleRenderType INSTANCE = new BeamParticleRenderType();

        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

            RenderSystem.disableCull();
            RenderSystem.depthMask(false);

            RenderSystem.setShaderTexture(0, FORTRON_TEXTURE);

            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }
    }
}
