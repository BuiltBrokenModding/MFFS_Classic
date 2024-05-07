package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetItemInSlotPacket(int slot, ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetItemInSlotPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("set_slot_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetItemInSlotPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        SetItemInSlotPacket::slot,
        ItemStack.STREAM_CODEC,
        SetItemInSlotPacket::stack,
        SetItemInSlotPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Player player = ctx.player();
        if (player.containerMenu != null) {
            player.containerMenu.getSlot(this.slot).set(this.stack);
        }
    }
}
