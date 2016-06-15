package com.mffs.model.packet;

import com.mffs.model.TileMFFS;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

/**
 * Created by pwaln on 6/14/2016.
 */
public class EntityToggle implements IMessage {

    /* The Redstone activation button opcode */
    public static final byte REDSTONE_TOGGLE = 1;

    /* This is the entity location */
    public int x, y, z;

    /* This is the opcode representing the field to toggle */
    public byte toggle_opcode;

    /**
     * Default constructor needed for reflection instiation
     */
    public EntityToggle(){}

    /**
     *
     * @param entity
     */
    public EntityToggle(TileEntity entity) {
        this.toggle_opcode = 0;
        this.x = entity.xCoord;
        this.y = entity.yCoord;
        this.z = entity.zCoord;
    }

    /**
     *
     * @param entity
     */
    public EntityToggle(TileEntity entity, byte subOp) {
        this.toggle_opcode = subOp;
        this.x = entity.xCoord;
        this.y = entity.yCoord;
        this.z = entity.zCoord;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.toggle_opcode = (byte) buf.readUnsignedByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(toggle_opcode);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    /**
     * Interrior class that handles this packet!
     */
    public static class Handler implements IMessageHandler<EntityToggle, IMessage> {
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
            if(entity instanceof TileMFFS) {
                return ((TileMFFS)entity).handleMessage(message);
            }
            return null;
        }
    }
}
