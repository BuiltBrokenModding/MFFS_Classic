package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateAnimationSpeed(BlockPos pos, int animationSpeed) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateAnimationSpeed> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("animation_speed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateAnimationSpeed> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        UpdateAnimationSpeed::pos,
        ByteBufCodecs.INT,
        UpdateAnimationSpeed::animationSpeed,
        UpdateAnimationSpeed::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
