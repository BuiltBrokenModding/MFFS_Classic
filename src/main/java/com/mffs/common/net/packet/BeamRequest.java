package com.mffs.common.net.packet;

import com.mffs.api.vector.Vector3D;
import com.mffs.common.TileMFFS;
import com.mffs.common.net.PositionMessage;
import com.mffs.common.tile.type.TileForceFieldProjector;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 7/6/2016.
 */
public class BeamRequest extends PositionMessage {

    /* This is the position we want the beam to go to. */
    public Vector3D destination;

    /**
     * Default constructor for class instance.
     */
    public BeamRequest() {
        super();
    }

    /**
     * @param entity
     */
    public BeamRequest(TileEntity entity, Vector3D vec) {
        super(entity);
        this.destination = vec;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        destination = new Vector3D(buf.readInt(), buf.readInt(), buf.readInt());
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(destination.intX()).writeInt(destination.intY()).writeInt(destination.intZ());
    }

    /**
     * Sends a sync to the Client.
     */
    public static class ClientHandler implements IMessageHandler<BeamRequest, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(BeamRequest message, MessageContext ctx) {
            TileEntity entity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileForceFieldProjector) {
                return ((TileForceFieldProjector) entity).handleMessage(message);
            }
            return null;
        }
    }
}
