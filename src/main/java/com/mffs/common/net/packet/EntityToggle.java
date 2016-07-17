package com.mffs.common.net.packet;

import com.mffs.common.net.TileEntityMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/14/2016.
 */
public class EntityToggle extends TileEntityMessage {

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
     * Server Version.
     */
    public static class ServerHandler extends TileEntityMessage.ServerHandler<EntityToggle> {
    }
}
