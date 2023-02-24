package dev.su5ed.mffs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public void processClientPacket(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleUpdateAnimationSpeedPacket(this));
    }
}
