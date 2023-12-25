package dev.su5ed.mffs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;

public record UpdateAnimationSpeed(BlockPos pos, int animationSpeed) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.animationSpeed);
    }

    public static UpdateAnimationSpeed decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int animationSpeed = buf.readInt();
        return new UpdateAnimationSpeed(pos, animationSpeed);
    }

    public void processClientPacket(NetworkEvent.Context ctx) {
        if (FMLEnvironment.dist.isClient()) {
            ClientPacketHandler.handleUpdateAnimationSpeedPacket(this);
        }
    }
}
