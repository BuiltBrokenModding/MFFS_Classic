package dev.su5ed.mffs.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BeamParticleProvider implements ParticleProvider<BeamParticleOptions> {
    private final SpriteSet sprites;
    
    public BeamParticleProvider(SpriteSet spriteSet) {
        this.sprites = spriteSet;
    }
    
    @Nullable
    @Override
    public Particle createParticle(BeamParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new BeamParticle(level, this.sprites, new Vec3(x, y, z), options.target, options.lifetime);
    }
}
