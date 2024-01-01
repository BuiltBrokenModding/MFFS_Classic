package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateAnimationSpeed(BlockPos pos, int animationSpeed) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("animation_speed");

    public UpdateAnimationSpeed(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.animationSpeed);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
