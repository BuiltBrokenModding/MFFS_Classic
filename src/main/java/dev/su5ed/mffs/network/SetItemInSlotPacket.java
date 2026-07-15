package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.util.inventory.SlotInventoryFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.Slot;
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
        if (ctx.flow().isClientbound()) {
            return;
        }
        var player = ctx.player();
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
