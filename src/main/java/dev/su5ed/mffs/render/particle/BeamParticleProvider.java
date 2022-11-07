package dev.su5ed.mffs.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BeamParticleProvider implements ParticleProvider<BeamParticleOptions> {

    @Nullable
    @Override
    public Particle createParticle(BeamParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new BeamParticle(level, new Vec3(x, y, z), options.target(), options.color(), options.lifetime());
    }
}
