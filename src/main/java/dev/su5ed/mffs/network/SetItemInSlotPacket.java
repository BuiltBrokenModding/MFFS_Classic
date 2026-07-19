package dev.su5ed.mffs.network;

import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.util.inventory.SlotInventoryFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (!(player.containerMenu instanceof FortronMenu<?> menu)) {
            return;
        }
        if (this.slot < 0 || this.slot >= menu.slots.size()) {
            return;
        }
        Slot target = menu.getSlot(this.slot);
        if (!(target instanceof SlotInventoryFilter)) {
            return;
        }
        target.set(this.stack);
    }
}
