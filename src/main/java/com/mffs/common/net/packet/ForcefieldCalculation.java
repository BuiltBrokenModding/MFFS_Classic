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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pwaln on 7/3/2016.
 */
public class ForcefieldCalculation extends PositionMessage {

    /* List of vectors to be assigned. */
    private Set<Vector3D> blocks;

    /**
     * Default constructor for class instance.
     */
    public ForcefieldCalculation() {
        super();
    }

    /**
     * @param proj
     */
    public ForcefieldCalculation(TileForceFieldProjector proj) {
        super(proj);
        this.blocks = proj.getCalculatedField();
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        blocks = new HashSet<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
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
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(blocks.size());
        for (Vector3D vec : blocks) {
            buf.writeInt(vec.intX());
            buf.writeInt(vec.intY());
            buf.writeInt(vec.intZ());
        }
    }

    public Set<Vector3D> getBlocks() {
        return blocks;
    }

    /**
     * Sends a sync to the Client.
     */
    public static class ClientHandler implements IMessageHandler<ForcefieldCalculation, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(ForcefieldCalculation message, MessageContext ctx) {
            TileEntity entity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.x, message.y, message.z);
            if (entity instanceof TileForceFieldProjector) {
                return ((TileMFFS) entity).handleMessage(message);
            }
            return null;
        }
    }
}
