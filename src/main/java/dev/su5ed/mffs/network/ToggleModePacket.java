package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.Activatable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleModePacket implements IMessage {
    private BlockPos pos;
    private boolean active;

    /** No-arg constructor required by Forge. */
    public ToggleModePacket() {}

    public ToggleModePacket(BlockPos pos, boolean active) {
        this.pos = pos;
        this.active = active;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos    = pb.readBlockPos();
        this.active = pb.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeBoolean(this.active);
    }

    public static class Handler implements IMessageHandler<ToggleModePacket, IMessage> {
        @Override
        public IMessage onMessage(ToggleModePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() ->
                Network.findTileEntity(Activatable.class, world, message.pos)
                    .ifPresent(be -> be.setActive(message.active))
            );
            return null;
        }
    }
}

