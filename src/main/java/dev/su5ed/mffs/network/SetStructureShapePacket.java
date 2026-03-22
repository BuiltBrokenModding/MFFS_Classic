package dev.su5ed.mffs.network;

import dev.su5ed.mffs.render.CustomProjectorModeClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetStructureShapePacket implements IMessage {
    private int dimension;
    private String structId;
    /** Null means clear/remove the shape from the client cache. */
    private Set<BlockPos> shape; // nullable

    public SetStructureShapePacket() {}

    /** Convenience constructor accepting any Collection<BlockPos>. shape may be null to clear. */
    public SetStructureShapePacket(int dimension, String structId, Collection<BlockPos> shape) {
        this.dimension = dimension;
        this.structId  = structId;
        this.shape     = shape != null ? new HashSet<>(shape) : null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.dimension  = pb.readInt();
        this.structId   = pb.readString(256);
        boolean hasShape = pb.readBoolean();
        if (hasShape) {
            int count = pb.readInt();
            this.shape = new HashSet<>(count);
            for (int i = 0; i < count; i++) {
                this.shape.add(BlockPos.fromLong(pb.readLong()));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(this.dimension);
        pb.writeString(this.structId);
        pb.writeBoolean(this.shape != null);
        if (this.shape != null) {
            pb.writeInt(this.shape.size());
            for (BlockPos pos : this.shape) {
                pb.writeLong(pos.toLong());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<SetStructureShapePacket, IMessage> {
        @Override
        public IMessage onMessage(SetStructureShapePacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
                CustomProjectorModeClientHandler.setShape(message.dimension, message.structId, message.shape)
            );
            return null;
        }
    }
}
