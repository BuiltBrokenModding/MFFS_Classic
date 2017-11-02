package com.builtbroken.mffs.common.net.packet;

import com.builtbroken.mffs.common.net.ItemMessage;
import io.netty.buffer.ByteBuf;

/**
 * Created by pwaln on 7/5/2016.
 */
public class ItemByteToggle extends ItemMessage
{
    /* Toggleid */
    public byte toggleId;

    public ItemByteToggle()
    {
    }

    public ItemByteToggle(int perm)
    {
        this.toggleId = (byte) perm;
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
        toggleId = buf.readByte();
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
        buf.writeByte(toggleId);
    }

    /**
     * aa
     */
    public static class ServerHandler extends ItemMessage.ServerHandler<ItemByteToggle>
    {
    }
}
