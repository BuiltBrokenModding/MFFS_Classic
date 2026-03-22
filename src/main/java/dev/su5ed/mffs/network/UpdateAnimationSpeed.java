package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdateAnimationSpeed implements IMessage {
    private BlockPos pos;
    private int animationSpeed;

    public UpdateAnimationSpeed() {}

    public UpdateAnimationSpeed(BlockPos pos, int animationSpeed) {
        this.pos            = pos;
        this.animationSpeed = animationSpeed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb    = new PacketBuffer(buf);
        this.pos            = pb.readBlockPos();
        this.animationSpeed = pb.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeInt(this.animationSpeed);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<UpdateAnimationSpeed, IMessage> {
        @Override
        public IMessage onMessage(UpdateAnimationSpeed message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
                Network.findTileEntity(ProjectorBlockEntity.class, mc.world, message.pos)
                    .ifPresent(be -> be.setClientAnimationSpeed(message.animationSpeed))
            );
            return null;
        }
    }
}

