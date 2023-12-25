package dev.su5ed.mffs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;

public record UpdateBlockEntityPacket(BlockPos pos, CompoundTag data) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(this.data);
    }

    public static UpdateBlockEntityPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag data = buf.readNbt();
        return new UpdateBlockEntityPacket(pos, data);
    }

    public void processClientPacket(NetworkEvent.Context ctx) {
        if (FMLEnvironment.dist.isClient()) {
            ClientPacketHandler.handleBlockEntityUpdatePacket(this);
        }
    }
}
