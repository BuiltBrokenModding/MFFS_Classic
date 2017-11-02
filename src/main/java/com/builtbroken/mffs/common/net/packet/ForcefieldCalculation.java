package com.builtbroken.mffs.common.net.packet;

import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.net.TileEntityMessage;
import com.builtbroken.mffs.content.projector.TileForceFieldProjector;
import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pwaln on 7/3/2016.
 */
public class ForcefieldCalculation extends TileEntityMessage
{

    /* List of vectors to be assigned. */
    private Set<Vector3D> blocks;

    /**
     * Default constructor for class instance.
     */
    public ForcefieldCalculation()
    {
        super();
    }

    /**
     * @param proj
     */
    public ForcefieldCalculation(TileForceFieldProjector proj)
    {
        super(proj);
        this.blocks = proj.getCalculatedField();
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
        blocks = new HashSet<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            Vector3D vec = new Vector3D(buf.readInt(), buf.readInt(), buf.readInt());
            blocks.add(vec);
        }
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
        buf.writeInt(blocks.size());
        for (Vector3D vec : blocks)
        {
            buf.writeInt(vec.intX());
            buf.writeInt(vec.intY());
            buf.writeInt(vec.intZ());
        }
    }

    public Set<Vector3D> getBlocks()
    {
        return blocks;
    }

    /**
     * Sends a sync to the Client.
     */
    public static class ClientHandler extends TileEntityMessage.ClientHandler<ForcefieldCalculation>
    {
    }
}
