package dev.su5ed.mffs.network;

import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
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
        NetworkEvent.Context context = ctx.get();
        ServerPlayer sender = context.getSender();
        if (sender.level.isLoaded(this.pos)) {
            sender.level.getBlockEntity(this.pos, ModObjects.FORCE_FIELD_BLOCK_ENTITY.get()).ifPresent(be -> {
                CompoundTag data = be.getCustomUpdateTag();
                UpdateBlockEntityPacket packet = new UpdateBlockEntityPacket(this.pos, data);
                Network.INSTANCE.reply(packet, context);
            });
        }
    }
}
