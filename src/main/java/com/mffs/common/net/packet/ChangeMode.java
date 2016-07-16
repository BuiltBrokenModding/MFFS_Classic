package com.mffs.common.net.packet;

import com.mffs.common.net.PositionMessage;
import com.mffs.common.tile.TileFrequency;
import com.mffs.common.tile.type.TileFortronCapacitor;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 7/5/2016.
 */
public class ChangeMode extends PositionMessage {

    /* New value for the transfer mode */
    private byte toggle;

    /**
     * Default constructor for class instance.
     */
    public ChangeMode() {
        super();
    }

    /**
     * @param entity
     */
    public ChangeMode(TileEntity entity, int mode) {
        super(entity);
        toggle = (byte) mode; // 255
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.toggle = buf.readByte();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(toggle);
    }

    public byte getToggle() {
        return toggle;
    }

    /**
     * Reads the message and handles it server side.
     */
    public static class ServerHandler implements IMessageHandler<ChangeMode, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(ChangeMode message, MessageContext ctx) {
            TileEntity entity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileFrequency) {
                return ((TileFrequency) entity).handleMessage(message);
            }
            return null;
        }
    }
}
