package com.builtbroken.mffs.common.net.packet;

import com.builtbroken.mffs.common.net.TileEntityMessage;
import com.builtbroken.mffs.common.tile.TileFortron;
import io.netty.buffer.ByteBuf;

/**
 * Created by pwaln on 6/14/2016.
 */
public class FortronSync extends TileEntityMessage
{

    /* Amount of fortron to be sent */
    public int amount, capacity;

    /**
     * Default Constructor
     */
    public FortronSync()
    {
        super();
    }

    public FortronSync(TileFortron tile)
    {
        super(tile);
        amount = tile.getTank().getFluidAmount();
        capacity = tile.getTank().getCapacity();
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        amount = buf.readInt();
        capacity = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(amount);
        buf.writeInt(capacity);
    }

    /**
     * FortronSync handler.
     */
    public static class ClientHandler extends TileEntityMessage.ClientHandler<FortronSync>
    {
    }
}
