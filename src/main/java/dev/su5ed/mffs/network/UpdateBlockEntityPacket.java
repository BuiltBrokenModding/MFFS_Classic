package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateBlockEntityPacket(BlockPos pos, CompoundTag data) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("update_block");

    public UpdateBlockEntityPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(this.data);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
