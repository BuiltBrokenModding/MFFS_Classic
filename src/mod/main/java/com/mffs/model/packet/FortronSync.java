package com.mffs.model.packet;

import com.mffs.model.TileMFFS;
import com.mffs.model.tile.TileFortron;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/14/2016.
 */
public class FortronSync implements IMessage {

    /* Amount of fortron to be sent */
    public int amount, capacity;
    /* The entity coordinates */
    private int x, y, z;

    /**
     * Default Constructor
     */
    public FortronSync() {
    }

    public FortronSync(TileFortron tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
        amount = tile.getTank().getFluidAmount();
        capacity = tile.getTank().getCapacity();
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        amount = buf.readInt();
        capacity = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(amount);
        buf.writeInt(capacity);
    }

    /**
     * FortronSync handler.
     */
    public static class Handler implements IMessageHandler<FortronSync, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(FortronSync message, MessageContext ctx) {
            TileEntity entity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileMFFS) {
                return ((TileMFFS) entity).handleMessage(message);
            }
            return null;
        }
    }
}
