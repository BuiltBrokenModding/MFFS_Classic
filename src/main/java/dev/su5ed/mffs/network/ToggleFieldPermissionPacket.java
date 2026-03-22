package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleFieldPermissionPacket implements IMessage {
    private BlockPos pos;
    private FieldPermission permission;
    private boolean value;

    public ToggleFieldPermissionPacket() {}

    public ToggleFieldPermissionPacket(BlockPos pos, FieldPermission permission, boolean value) {
        this.pos        = pos;
        this.permission = permission;
        this.value      = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb   = new PacketBuffer(buf);
        this.pos          = pb.readBlockPos();
        this.permission   = FieldPermission.values()[pb.readInt()];
        this.value        = pb.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        pb.writeInt(this.permission.ordinal());
        pb.writeBoolean(this.value);
    }

    public static class Handler implements IMessageHandler<ToggleFieldPermissionPacket, IMessage> {
        @Override
        public IMessage onMessage(ToggleFieldPermissionPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() ->
                Network.findTileEntity(BiometricIdentifierBlockEntity.class, world, message.pos)
                    .flatMap(BiometricIdentifierBlockEntity::getManipulatingCard)
                    .ifPresent(card -> {
                        if (message.value) card.addPermission(message.permission);
                        else               card.removePermission(message.permission);
                    })
            );
            return null;
        }
    }
}

