package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.util.TransferMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SwitchTransferModePacket implements IMessage {
    private BlockPos pos;
    private TransferMode mode;

    public SwitchTransferModePacket() {}

    public SwitchTransferModePacket(BlockPos pos, TransferMode mode) {
        this.pos  = pos;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos  = pb.readBlockPos();
        this.mode = TransferMode.values()[pb.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeInt(this.mode.ordinal());
    }

    public static class Handler implements IMessageHandler<SwitchTransferModePacket, IMessage> {
        @Override
        public IMessage onMessage(SwitchTransferModePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() ->
                Network.findTileEntity(FortronCapacitorBlockEntity.class, world, message.pos)
                    .ifPresent(be -> be.setTransferMode(message.mode))
            );
            return null;
        }
    }
}

