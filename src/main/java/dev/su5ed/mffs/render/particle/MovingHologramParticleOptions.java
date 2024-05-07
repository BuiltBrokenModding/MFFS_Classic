package dev.su5ed.mffs.render.particle;

import com.mojang.serialization.Codec;
import dev.su5ed.mffs.setup.ModObjects;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

public record MovingHologramParticleOptions(ParticleColor color, int lifetime) implements ParticleOptions {
    public static final MovingHologramParticleOptions DEFAULT = new MovingHologramParticleOptions(ParticleColor.WHITE, 20);
    public static final Codec<MovingHologramParticleOptions> CODEC = Codec.unit(DEFAULT);
    public static final StreamCodec<ByteBuf, MovingHologramParticleOptions> STREAM_CODEC = StreamCodec.unit(DEFAULT);

    @Override
    public ParticleType<?> getType() {
        return ModObjects.MOVING_HOLOGRAM_PARTICLE.get();
    }
}
