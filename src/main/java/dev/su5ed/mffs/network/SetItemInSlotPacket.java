package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SetItemInSlotPacket(int slot, ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("set_slot_item");

    public SetItemInSlotPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readItem());
    }

    public void handle(PlayPayloadContext ctx) {
        Player player = ctx.player().orElseThrow();
        if (player.containerMenu != null) {
            player.containerMenu.getSlot(this.slot).set(this.stack);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeItem(this.stack);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
