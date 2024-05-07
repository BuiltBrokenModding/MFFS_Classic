package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateBlockEntityPacket(BlockPos pos, CompoundTag data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateBlockEntityPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("update_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBlockEntityPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        UpdateBlockEntityPacket::pos,
        ByteBufCodecs.COMPOUND_TAG,
        UpdateBlockEntityPacket::data,
        UpdateBlockEntityPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
