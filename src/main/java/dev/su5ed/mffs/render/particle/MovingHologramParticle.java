package dev.su5ed.mffs.render.particle;

import dev.su5ed.mffs.render.particle.MovingHolograpParticleGroup.MovingHologramParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MovingHologramParticle extends Particle {
    private final ParticleColor color;

    public MovingHologramParticle(ClientLevel level, Vec3 pos, ParticleColor color, int lifetime) {
        super(level, pos.x(), pos.y(), pos.z(), 0, 0, 0);

        this.color = color;
        setLifetime(lifetime);
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            remove();
        }
    }

    @Override
    public ParticleRenderType getGroup() {
        return ModParticleRenderType.HOLO;
    }

    public MovingHologramParticleData extract(float partialTicks) {
        double x0 = Mth.lerp(partialTicks, this.xo, this.x);
        double y0 = Mth.lerp(partialTicks, this.yo, this.y);
        double z0 = Mth.lerp(partialTicks, this.zo, this.z);
        
        return new MovingHologramParticleData(this.color, this.age, this.lifetime, x0, y0, z0);
    }
}
