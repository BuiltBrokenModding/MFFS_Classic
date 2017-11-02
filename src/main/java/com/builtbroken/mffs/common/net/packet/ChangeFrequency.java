package com.builtbroken.mffs.common.net.packet;

import com.builtbroken.mffs.common.net.TileEntityMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/16/2016.
 */
public class ChangeFrequency extends TileEntityMessage
{

    /**
     * The Frequency.
     */
    private int frequency;

    /**
     * Default Constructor.
     */
    public ChangeFrequency()
    {
        super();
    }

    /**
     * @param entity
     * @param freq
     */
    public ChangeFrequency(TileEntity entity, int freq)
    {
        super(entity);
        this.frequency = freq;
    }

    public int getFrequency()
    {
        return frequency;
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
        frequency = buf.readInt();
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
        buf.writeInt(frequency);
    }

    /**
     * Reads the message and handles it server side.
     */
    public static class ServerHandler extends TileEntityMessage.ServerHandler<ChangeFrequency>
    {
    }
}
