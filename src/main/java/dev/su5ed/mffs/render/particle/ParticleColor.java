package dev.su5ed.mffs.render.particle;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum ParticleColor {
    BLUE_BEAM(153 / 255.0F, 153 / 255.0F, 1),
    BLUE_FIELD(52 / 255.0F, 254 / 255.0F, 1),
    RED(1, 0, 0),
    WHITE(1, 1, 1);

    public static final StreamCodec<FriendlyByteBuf, ParticleColor> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ParticleColor.class);
    
    private final float red;
    private final float green;
    private final float blue;

    ParticleColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }
}
