package com.mffs.common.net.packet;

import com.mffs.api.vector.Vector3D;
import com.mffs.common.TileMFFS;
import com.mffs.common.blocks.BlockForceField;
import com.mffs.common.net.PositionMessage;
import com.mffs.common.tile.TileFrequency;
import com.mffs.common.tile.type.TileForceField;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by pwaln on 7/11/2016.
 */
public final class RefreshCamo extends PositionMessage {
    /* This is the location of the projector */
    public Vector3D projector;

    /**
     * Default constructor for class instance.
     */
    public RefreshCamo() {
        super();
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public RefreshCamo(World world, int x, int y, int z) {
        super(x, y, z);
        TileEntity entity = world.getTileEntity(x, y, z);
        if(entity instanceof TileForceField)
            this.projector = ((TileForceField)entity).getProjLoc();
        else
            this.projector = Vector3D.ZERO();
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        projector = new Vector3D(buf.readInt(), buf.readInt(), buf.readInt());
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(projector.intX()).writeInt(projector.intY()).writeInt(projector.intZ());
    }

    /**
     * Sends a sync to the Client.
     */
    public static class ClientHandler implements IMessageHandler<RefreshCamo, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(RefreshCamo message, MessageContext ctx) {
            TileEntity entity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileForceField) {
                return ((TileForceField) entity).handleMessage(message);
            }
            return null;
        }
    }

    /**
     * Reads the message and handles it server side.
     */
    public static class ServerHandler implements IMessageHandler<RefreshCamo, RefreshCamo> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public RefreshCamo onMessage(RefreshCamo message, MessageContext ctx) {
            TileEntity entity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileForceField) {
                return (RefreshCamo) ((TileForceField) entity).handleMessage(message);
            }
            return null;
        }
    }
}
