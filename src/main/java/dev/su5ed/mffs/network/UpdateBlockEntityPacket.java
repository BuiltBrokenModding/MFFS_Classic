package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdateBlockEntityPacket implements IMessage {
    private BlockPos pos;
    private NBTTagCompound data;

    public UpdateBlockEntityPacket() {}

    public UpdateBlockEntityPacket(BlockPos pos, NBTTagCompound data) {
        this.pos  = pos;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos  = pb.readBlockPos();
        try {
            this.data = pb.readCompoundTag();
        } catch (java.io.IOException e) {
            this.data = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeCompoundTag(this.data);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<UpdateBlockEntityPacket, IMessage> {
        @Override
        public IMessage onMessage(UpdateBlockEntityPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
                Network.findTileEntity(ForceFieldBlockEntity.class, mc.world, message.pos)
                    .ifPresent(be -> be.handleCustomUpdateTag(message.data))
            );
            return null;
        }
    }
}

