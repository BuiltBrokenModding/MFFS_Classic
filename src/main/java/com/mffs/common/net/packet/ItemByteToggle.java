package com.mffs.common.net.packet;

import com.mffs.common.net.IPacketReceiver;
import com.mffs.common.net.ItemMessage;
import com.mffs.common.net.TileEntityMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 7/5/2016.
 */
public class ItemByteToggle extends ItemMessage {

    /* Toggleid */
    private byte toggleId;

    public ItemByteToggle() {}

    public ItemByteToggle(int perm) {
        this.toggleId = (byte) perm;
    }


    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        toggleId = buf.readByte();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(toggleId);
    }

    /**
     * aa
     */
    public static class ServerHandler extends ItemMessage.ServerHandler<ItemByteToggle> {}
}
