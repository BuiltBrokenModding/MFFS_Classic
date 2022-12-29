package dev.su5ed.mffs.render.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public record MovingHologramParticleOptions(Vec3 target, BeamColor color, int lifetime) implements ParticleOptions {
    public static final MovingHologramParticleOptions DEFAULT = new MovingHologramParticleOptions(Vec3.ZERO, BeamColor.BLUE, 20);
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
        return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString();
    }
}
