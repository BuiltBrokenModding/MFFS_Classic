package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SwitchConfiscationModePacket implements IMessage {
    private BlockPos pos;
    private InterdictionMatrix.ConfiscationMode mode;

    public SwitchConfiscationModePacket() {}

    public SwitchConfiscationModePacket(BlockPos pos, InterdictionMatrix.ConfiscationMode mode) {
        this.pos  = pos;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos  = pb.readBlockPos();
        this.mode = InterdictionMatrix.ConfiscationMode.values()[pb.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeInt(this.mode.ordinal());
    }

    public static class Handler implements IMessageHandler<SwitchConfiscationModePacket, IMessage> {
        @Override
        public IMessage onMessage(SwitchConfiscationModePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() ->
                Network.findTileEntity(InterdictionMatrixBlockEntity.class, world, message.pos)
                    .ifPresent(be -> be.setConfiscationMode(message.mode))
            );
            return null;
        }
    }
}

