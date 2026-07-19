package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateInventoryPacket(BlockPos pos, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<UpdateInventoryPacket> TYPE = new Type<>(MFFSMod.location("update_inventory"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateInventoryPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        UpdateInventoryPacket::pos,
        ByteBufCodecs.COMPOUND_TAG,
        UpdateInventoryPacket::tag,
        UpdateInventoryPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
