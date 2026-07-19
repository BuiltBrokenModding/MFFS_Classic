package dev.su5ed.mffs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public record SetBlockItemInSlotPacket(BlockPos pos, int slot, ItemStack stack) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.slot);
        buf.writeItem(this.stack);
    }

    public static SetBlockItemInSlotPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int slot = buf.readInt();
        ItemStack stack = buf.readItem();
        return new SetBlockItemInSlotPacket(pos, slot, stack);
    }

    public void processClientPacket(Supplier<Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSetBlockItemInSlotPacket(this));
    }
}
