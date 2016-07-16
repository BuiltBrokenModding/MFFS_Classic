package com.mffs.common.net.packet;

import com.mffs.common.TileMFFS;
import com.mffs.common.net.PositionMessage;
import com.mffs.common.tile.TileFrequency;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Poopsicle360 on 7/15/2016.
 */
public final class StringModify extends PositionMessage {

    /* The username to be sent */
    public String username;

    /**
     * Default constructor for class instance.
     */
    public StringModify() {
        super();
    }

    /**
     * @param entity
     */
    public StringModify(TileEntity entity, String name) {
        super(entity);
        this.username = name;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        username = ByteBufUtils.readUTF8String(buf);
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, username);
    }

    /**
     * Reads the message and handles it server side.
     */
    public static class ServerHandler implements IMessageHandler<StringModify, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(StringModify message, MessageContext ctx) {
            TileEntity entity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileMFFS) {
                return ((TileMFFS) entity).handleMessage(message);
            }
            return null;
        }
    }
}
