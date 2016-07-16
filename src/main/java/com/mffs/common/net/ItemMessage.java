package com.mffs.common.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
public abstract class ItemMessage implements IMessage {

    /* The slot we are referencing */
    public byte slot;

    public ItemMessage() {this.slot = -1;}

    public ItemMessage(int slot) {
        this.slot = (byte) slot;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        buf.writeByte(slot);
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        slot = buf.readByte();
    }

    /**
     *
     * @param <PACKET>
     */
    protected static class ServerHandler<PACKET extends ItemMessage> implements IMessageHandler<PACKET, IMessage> {

        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(PACKET message, MessageContext ctx) {
            EntityPlayerMP mp = ctx.getServerHandler().playerEntity;
            if(mp != null) {
                ItemStack item = (message.slot >= 0 ? mp.inventory.getStackInSlot(message.slot) : mp.getCurrentEquippedItem());
                if(item != null && item.getItem() instanceof  IPacketReceiver) {
                    return ((IPacketReceiver) item.getItem()).handleMessage(message);
                }
            }
            return null;
        }
    }
}
