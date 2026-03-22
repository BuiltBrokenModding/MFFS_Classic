package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InitialDataRequestPacket implements IMessage {
    private BlockPos pos;

    public InitialDataRequestPacket() {}

    public InitialDataRequestPacket(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new PacketBuffer(buf).readBlockPos();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        new PacketBuffer(buf).writeBlockPos(this.pos);
    }

    public static class Handler implements IMessageHandler<InitialDataRequestPacket, IMessage> {
        @Override
        public IMessage onMessage(InitialDataRequestPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() -> {
                if (world.isBlockLoaded(message.pos)) {
                    Network.findTileEntity(ForceFieldBlockEntity.class, world, message.pos).ifPresent(be -> {
                        NBTTagCompound data = be.getCustomUpdateTag();
                        Network.sendTo(new UpdateBlockEntityPacket(message.pos, data), player);
                    });
                }
            });
            return null;
        }
    }
}

