package dev.su5ed.mffs.render.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record MovingHologramParticleOptions(ParticleColor color, int lifetime) implements ParticleOptions {
    public static final MovingHologramParticleOptions DEFAULT = new MovingHologramParticleOptions(ParticleColor.WHITE, 20);
    @SuppressWarnings("deprecation")
    public static final Deserializer<MovingHologramParticleOptions> DESERIALIZER = new Deserializer<>() {
        public MovingHologramParticleOptions fromCommand(ParticleType<MovingHologramParticleOptions> type, StringReader reader) {
            return DEFAULT;
        }

        public MovingHologramParticleOptions fromNetwork(ParticleType<MovingHologramParticleOptions> type, FriendlyByteBuf buf) {
            return DEFAULT;
        }
    };
    public static final Codec<MovingHologramParticleOptions> CODEC = Codec.unit(DEFAULT);

    @Override
    public ParticleType<?> getType() {
        return ModObjects.MOVING_HOLOGRAM_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {}

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()).toString();
    }
}
