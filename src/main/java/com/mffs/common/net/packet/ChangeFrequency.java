package com.mffs.common.net.packet;

import com.mffs.common.TileMFFS;
import com.mffs.common.net.PositionMessage;
import com.mffs.common.tile.TileFrequency;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/16/2016.
 */
public class ChangeFrequency extends PositionMessage {

    /**
     * The Frequency.
     */
    private int frequency;

    /**
     * Default Constructor.
     */
    public ChangeFrequency() {
        super();
    }

    /**
     * @param entity
     * @param freq
     */
    public ChangeFrequency(TileEntity entity, int freq) {
        super(entity);
        this.frequency = freq;
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        frequency = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(frequency);
    }

    /**
     * Reads the message and handles it server side.
     */
    public static class ServerHandler implements IMessageHandler<ChangeFrequency, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(ChangeFrequency message, MessageContext ctx) {
            TileEntity entity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileFrequency) {
                return ((TileFrequency) entity).handleMessage(message);
            }
            return null;
        }
    }
}
