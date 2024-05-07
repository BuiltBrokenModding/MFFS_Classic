package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.render.particle.ParticleColor;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record DrawBeamPacket(Vec3 target, Vec3 position, ParticleColor color, int lifetime) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DrawBeamPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("draw_beam"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DrawBeamPacket> STREAM_CODEC = StreamCodec.composite(
        ModUtil.VEC3_STREAM_CODEC,
        DrawBeamPacket::target,
        ModUtil.VEC3_STREAM_CODEC,
        DrawBeamPacket::position,
        ParticleColor.STREAM_CODEC,
        DrawBeamPacket::color,
        ByteBufCodecs.INT,
        DrawBeamPacket::lifetime,
        DrawBeamPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
