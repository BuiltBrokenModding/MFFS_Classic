package com.mffs.common.net.packet;

import com.mffs.common.TileMFFS;
import com.mffs.common.net.PositionMessage;
import com.mffs.common.tile.TileModuleAcceptor;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 7/2/2016.
 */
public class FortronCostSync extends PositionMessage {
    /* Fortron cost */
    private int cost;

    public FortronCostSync() {
        this.cost = 0;
    }

    /**
     * @param module
     * @param cost
     */
    public FortronCostSync(TileModuleAcceptor module, int cost) {
        super(module);
        this.cost = 0;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.cost = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(cost);
    }

    /**
     * Sends a sync to the Client.
     */
    public static class ClientHandler implements IMessageHandler<FortronCostSync, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(FortronCostSync message, MessageContext ctx) {
            TileEntity entity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileMFFS) {
                return ((TileMFFS) entity).handleMessage(message);
            }
            return null;
        }
    }
}
