package dev.su5ed.mffs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record InitialDataRequestPacket(BlockPos pos) {

    public static void encode(InitialDataRequestPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
    }

    public static InitialDataRequestPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return new InitialDataRequestPacket(pos);
    }

    public void processPacket(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer sender = ctx.get().getSender();

        if (sender.level.isLoaded(this.pos)) {
            BlockEntity be = sender.level.getBlockEntity(this.pos);
            CompoundTag data = be.getUpdateTag();
            UpdateBlockEntityPacket packet = new UpdateBlockEntityPacket(this.pos, data);
            Network.INSTANCE.reply(packet, ctx.get());
        }
    }
}
