package com.mffs.common.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/16/2016.
 */
public abstract class TileEntityMessage implements IMessage {

    /* Location index for entity */
    protected int x, y, z;

    /**
     * Default constructor for class instance.
     */
    public TileEntityMessage() {
    }

    /**
     * @param entity
     */
    public TileEntityMessage(TileEntity entity) {
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
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
