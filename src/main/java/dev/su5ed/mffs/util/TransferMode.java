package dev.su5ed.mffs.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

/**
 * The force field transfer mode.
 */
public enum TransferMode {
    EQUALIZE,
    DISTRIBUTE,
    DRAIN,
    FILL;

    private static final TransferMode[] VALUES = values();
    public static final StreamCodec<FriendlyByteBuf, TransferMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(TransferMode.class);

    public TransferMode next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }
}
