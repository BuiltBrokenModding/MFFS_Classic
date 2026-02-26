package dev.su5ed.mffs.render.particle;

import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BeamParticle extends Particle {
    private static final int ROTATION_SPEED = 20;
    private static final boolean PULSE = true;

    private final float length;
    private final float rotYaw;
    private final float rotPitch;
    private final float prevYaw;
    private final float prevPitch;
    private final ParticleColor color;

    private float prevSize;

    public BeamParticle(ClientLevel level, Vec3 start, Vec3 target, ParticleColor color, int lifetime) {
        super(level, start.x(), start.y(), start.z(), 0, 0, 0);

        setSize(0.02f, 0.02f);
        setBoundingBox(new AABB(start, target));
        setLifetime(lifetime);

        this.xd = this.xo - target.x();
        this.yd = this.yo - target.y();
        this.zd = this.zo - target.z();
        this.length = (float) start.distanceTo(target);
        this.rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / Math.PI);
        double destX = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
        this.rotPitch = (float) (Math.atan2(yd, destX) * 180.0D / Math.PI);
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;
        this.color = color;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            remove();
        }
    }

    @Override
    public ParticleRenderType getGroup() {
        return ModParticleRenderType.BEAM;
    }

    public BeamParticleGroup.BeamParticleData extract(Camera camera, float partialTicks) {
        Matrix4f mat = new Matrix4f();
        mat.identity();

        int deg = 360 / ROTATION_SPEED;
        float rot = this.level.getGameTime() % deg * ROTATION_SPEED + ROTATION_SPEED * partialTicks;

        float size = 1.0f;
        if (PULSE) {
            size = Math.min(this.age / 4.0F, 1.0F);
            size = Mth.lerp(partialTicks, this.prevSize, size);
        }

        float opacity = 0.5F;
        if (PULSE && this.lifetime - this.age <= 4) {
            opacity = 0.5F - (4 - (this.lifetime - this.age)) * 0.1F;
        }

        float tickSlide = this.level.getGameTime() + partialTicks;
        float vOffset = -tickSlide * 0.2F - Mth.floor(-tickSlide * 0.1F);

        Vec3 vec3 = camera.position();
        float xx = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float yy = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float zz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        mat.translate(new Vector3f(xx, yy, zz));

        float ry = Mth.lerp(partialTicks, this.prevYaw, this.rotYaw);
        float rp = Mth.lerp(partialTicks, this.prevPitch, this.rotPitch);
        mat.rotate(Axis.XP.rotationDegrees(90));
        mat.rotate(Axis.ZN.rotationDegrees(180 + ry));
        mat.rotate(Axis.XP.rotationDegrees(rp));

        float xNegMin = -0.15f * size;
        float xNegMax = -0.15f * size;
        float xPosMin = 0.15f * size;
        float xPosMax = 0.15f * size;
        float yMax = this.length * size;

        Vector3f[] vectors = new Vector3f[]{
            new Vector3f(xNegMax, yMax, 0.0F),
            new Vector3f(xNegMin, 0.0F, 0.0F),
            new Vector3f(xPosMin, 0.0F, 0.0F),
            new Vector3f(xPosMax, yMax, 0.0F)
        };

        mat.rotate(Axis.YP.rotationDegrees(rot));
        
        this.prevSize = size;
        
        return new BeamParticleGroup.BeamParticleData(
            this.color,
            vectors,
            mat,
            vOffset,
            this.length,
            size,
            opacity
        );
    }
}
