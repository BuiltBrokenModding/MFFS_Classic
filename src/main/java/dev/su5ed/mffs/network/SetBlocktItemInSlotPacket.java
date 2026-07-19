package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record SetBlocktItemInSlotPacket(BlockPos pos, int slot, ItemStack stack) implements CustomPacketPayload {
    public static final Type<SetBlocktItemInSlotPacket> TYPE = new Type<>(MFFSMod.location("set_block_slot_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetBlocktItemInSlotPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        SetBlocktItemInSlotPacket::pos,
        ByteBufCodecs.INT,
        SetBlocktItemInSlotPacket::slot,
        ItemStack.OPTIONAL_STREAM_CODEC,
        SetBlocktItemInSlotPacket::stack,
        SetBlocktItemInSlotPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
