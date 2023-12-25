package dev.su5ed.mffs.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public record SetItemInSlotPacket(int slot, ItemStack stack) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeItem(this.stack);
    }

    public static SetItemInSlotPacket decode(FriendlyByteBuf buf) {
        int slot = buf.readInt();
        ItemStack stack = buf.readItem();
        return new SetItemInSlotPacket(slot, stack);
    }

    public void processServerPacket(NetworkEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player.containerMenu != null) {
            player.containerMenu.getSlot(this.slot).set(this.stack);
        }
    }
}
