package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.setup.ModCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateFrequencyPacket implements IMessage {
    private BlockPos pos;
    private int frequency;

    public UpdateFrequencyPacket() {}

    public UpdateFrequencyPacket(BlockPos pos, int frequency) {
        this.pos       = pos;
        this.frequency = frequency;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos       = pb.readBlockPos();
        this.frequency = pb.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeInt(this.frequency);
    }

    public static class Handler implements IMessageHandler<UpdateFrequencyPacket, IMessage> {
        @Override
        public IMessage onMessage(UpdateFrequencyPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() ->
                Network.findTileEntity(world, message.pos).ifPresent(te -> {
                    FortronStorage fortron = te.getCapability(ModCapabilities.FORTRON, null);
                    if (fortron != null) {
                        fortron.setFrequency(message.frequency);
                    }
                })
            );
            return null;
        }
    }
}

