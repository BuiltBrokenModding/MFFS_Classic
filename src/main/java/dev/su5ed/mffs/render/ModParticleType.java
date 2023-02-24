package dev.su5ed.mffs.render;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ModParticleType<T extends ParticleOptions> extends ParticleType<T> {
    private final Codec<T> codec;

    @SuppressWarnings("deprecation")
    public ModParticleType(boolean overrideLimiter, ParticleOptions.Deserializer<T> deserializer, Codec<T> codec) {
        super(overrideLimiter, deserializer);

        this.codec = codec;
    }

    @Override
    public Codec<T> codec() {
        return this.codec;
    }
}
