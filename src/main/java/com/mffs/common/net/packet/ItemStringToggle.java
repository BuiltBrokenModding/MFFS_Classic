package com.mffs.common.net.packet;

import com.mffs.common.TileMFFS;
import com.mffs.common.net.ItemMessage;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Poopsicle360 on 7/15/2016.
 */
public final class ItemStringToggle extends ItemMessage {

    /* The text to be sent */
    public String text;

    /**
     * Default constructor for class instance.
     */
    public ItemStringToggle() {
        super();
    }

    public ItemStringToggle(String text) {
        this.text = text;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        text = ByteBufUtils.readUTF8String(buf);
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, text);
    }

    /**
     * Reads the message and handles it server side.
     */
    public static class ServerHandler extends ItemMessage.ServerHandler<ItemStringToggle> {}
}
