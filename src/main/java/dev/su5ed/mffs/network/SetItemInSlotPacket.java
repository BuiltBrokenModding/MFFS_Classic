package dev.su5ed.mffs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetItemInSlotPacket implements IMessage {
    private int slot;
    private ItemStack stack;

    public SetItemInSlotPacket() {}

    public SetItemInSlotPacket(int slot, ItemStack stack) {
        this.slot  = slot;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.slot  = pb.readInt();
        try {
            this.stack = pb.readItemStack();
        } catch (java.io.IOException e) {
            this.stack = ItemStack.EMPTY;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(this.slot);
        pb.writeItemStack(this.stack);
    }

    public static class Handler implements IMessageHandler<SetItemInSlotPacket, IMessage> {
        @Override
        public IMessage onMessage(SetItemInSlotPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() -> {
                if (player.openContainer != null) {
                    player.openContainer.getSlot(message.slot).putStack(message.stack);
                }
            });
            return null;
        }
    }
}

