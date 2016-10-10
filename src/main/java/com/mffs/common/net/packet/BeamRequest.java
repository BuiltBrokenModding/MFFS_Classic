package com.mffs.common.net.packet;

import com.mffs.api.vector.Vector3D;
import com.mffs.common.net.TileEntityMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 7/6/2016.
 */
public class BeamRequest extends TileEntityMessage {

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
    public static class ClientHandler extends TileEntityMessage.ClientHandler<BeamRequest> {}
}
