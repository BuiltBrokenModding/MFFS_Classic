package dev.su5ed.mffs.render;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class BeamParticleOptions implements ParticleOptions {
    public static final BeamParticleOptions DEFAULT = new BeamParticleOptions(new Vec3(0, 0, 0), 20);
    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<BeamParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public BeamParticleOptions fromCommand(ParticleType<BeamParticleOptions> type, StringReader reader) {
            return DEFAULT;
        }

        public BeamParticleOptions fromNetwork(ParticleType<BeamParticleOptions> type, FriendlyByteBuf buf) {
            return DEFAULT;
        }
    };
    public static final Codec<BeamParticleOptions> CODEC = Codec.unit(DEFAULT);

    public final Vec3 target;
    public final int lifetime;

    public BeamParticleOptions(Vec3 target, int lifetime) {
        this.target = target;
        this.lifetime = lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return ModObjects.BEAM_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {

    }

    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(getType()).toString();
    }
}
