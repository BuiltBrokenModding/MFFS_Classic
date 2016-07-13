package com.mffs.common.net.packet;

import com.mffs.common.TileMFFS;
import com.mffs.common.net.PositionMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/14/2016.
 */
public class EntityToggle extends PositionMessage {

    /* The Redstone activation button opcode */
    public static final byte REDSTONE_TOGGLE = 0, TOGGLE_STATE = 1, ABSOLUTE_TOGGLE = 2, TRANSFER_TOGGLE = 3;

    /* This is the opcode representing the field to toggle */
    public byte toggle_opcode = 0;

    public EntityToggle() {
        super();
    }

    /**
     * @param entity
     */
    public EntityToggle(TileEntity entity) {
        super(entity);
    }

    /**
     * @param entity
     */
    public EntityToggle(TileEntity entity, byte subOp) {
        super(entity);
        this.toggle_opcode = subOp;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.toggle_opcode = (byte) buf.readUnsignedByte();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(toggle_opcode);
    }

    /**
     * Reads the message and handles it server side.
     */
    public static class ServerHandler implements IMessageHandler<EntityToggle, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(EntityToggle message, MessageContext ctx) {
            TileEntity entity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileMFFS) {
                return ((TileMFFS) entity).handleMessage(message);
            }
            return null;
        }
    }
}
