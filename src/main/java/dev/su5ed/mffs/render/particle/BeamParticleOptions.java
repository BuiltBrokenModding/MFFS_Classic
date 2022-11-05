package dev.su5ed.mffs.render.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public record BeamParticleOptions(Vec3 target, BeamColor color, int lifetime) implements ParticleOptions {
    public static final BeamParticleOptions DEFAULT = new BeamParticleOptions(new Vec3(0, 0, 0), BeamColor.BLUE, 20);
    @SuppressWarnings("deprecation")
    public static final Deserializer<BeamParticleOptions> DESERIALIZER = new Deserializer<>() {
        public BeamParticleOptions fromCommand(ParticleType<BeamParticleOptions> type, StringReader reader) {
            return DEFAULT;
        }

        public BeamParticleOptions fromNetwork(ParticleType<BeamParticleOptions> type, FriendlyByteBuf buf) {
            return DEFAULT;
        }
    };
    public static final Codec<BeamParticleOptions> CODEC = Codec.unit(DEFAULT);

    @Override
    public ParticleType<?> getType() {
        return ModObjects.BEAM_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {}

    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString();
    }
}
