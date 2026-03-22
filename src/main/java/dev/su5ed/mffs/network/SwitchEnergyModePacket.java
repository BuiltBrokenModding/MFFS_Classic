package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SwitchEnergyModePacket implements IMessage {
    private BlockPos pos;
    private CoercionDeriverBlockEntity.EnergyMode mode;

    public SwitchEnergyModePacket() {}

    public SwitchEnergyModePacket(BlockPos pos, CoercionDeriverBlockEntity.EnergyMode mode) {
        this.pos  = pos;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos  = pb.readBlockPos();
        this.mode = CoercionDeriverBlockEntity.EnergyMode.values()[pb.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeInt(this.mode.ordinal());
    }

    public static class Handler implements IMessageHandler<SwitchEnergyModePacket, IMessage> {
        @Override
        public IMessage onMessage(SwitchEnergyModePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() ->
                Network.findTileEntity(CoercionDeriverBlockEntity.class, world, message.pos)
                    .ifPresent(be -> be.setEnergyMode(message.mode))
            );
            return null;
        }
    }
}

