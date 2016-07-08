package com.mffs.model.net.packet;

import com.mffs.api.vector.Vector3D;
import com.mffs.model.net.TileEntityMessage;
import com.mffs.model.tile.type.TileForceFieldProjector;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 7/6/2016.
 */
public class BeamRequest extends TileEntityMessage {

    /* This is the position we want the beam to go to. */
    private Vector3D destination;

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
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
    }
}
