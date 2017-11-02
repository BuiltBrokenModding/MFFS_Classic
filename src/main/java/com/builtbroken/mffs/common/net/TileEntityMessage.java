package com.builtbroken.mffs.common.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/16/2016.
 */
public abstract class TileEntityMessage implements IMessage
{

    /* Location index for entity */
    protected int x, y, z;

    /**
     * Default constructor for class instance.
     */
    public TileEntityMessage()
    {
    }

    /**
     * @param entity
     */
    public TileEntityMessage(TileEntity entity)
    {
        this.x = entity.xCoord;
        this.y = entity.yCoord;
        this.z = entity.zCoord;
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public TileEntityMessage(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    /**
     * Server Version.
     */
    public static class ServerHandler<PACKET extends TileEntityMessage> implements IMessageHandler<PACKET, IMessage>
    {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(PACKET message, MessageContext ctx)
        {
            TileEntity entity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof IPacketReceiver_Entity)
            {
                return ((IPacketReceiver_Entity) entity).handleMessage(message);
            }
            return null;
        }
    }

    /**
     * FortronSync handler.
     */
    public static class ClientHandler<PACKET extends TileEntityMessage> implements IMessageHandler<PACKET, IMessage>
    {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(PACKET message, MessageContext ctx)
        {
            TileEntity entity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof IPacketReceiver_Entity)
            {
                return ((IPacketReceiver_Entity) entity).handleMessage(message);
            }
            return null;
        }
    }
}
