package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record DrawHologramPacket(Vec3 pos, Vec3 target, Type holoType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DrawHologramPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("draw_hologram"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DrawHologramPacket> STREAM_CODEC = StreamCodec.composite(
        ModUtil.VEC3_STREAM_CODEC,
        DrawHologramPacket::pos,
        ModUtil.VEC3_STREAM_CODEC,
        DrawHologramPacket::target,
        Type.STREAM_CODEC,
        DrawHologramPacket::holoType,
        DrawHologramPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Type {
        CONSTRUCT,
        DESTROY;

        public static final StreamCodec<FriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Type.class);
    }
}
