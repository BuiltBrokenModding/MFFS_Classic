package dev.su5ed.mffs.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MovingHologramParticleProvider implements ParticleProvider<MovingHologramParticleOptions> {

    @Nullable
    @Override
    public Particle createParticle(MovingHologramParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
        return new MovingHologramParticle(level, new Vec3(x, y, z), options.color(), options.lifetime());
    }
}
