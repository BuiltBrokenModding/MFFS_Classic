package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InitialDataRequestPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<InitialDataRequestPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("initial_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InitialDataRequestPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        InitialDataRequestPacket::pos,
        InitialDataRequestPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
        if (level.isLoaded(this.pos)) {
            level.getBlockEntity(this.pos, ModObjects.FORCE_FIELD_BLOCK_ENTITY.get()).ifPresent(be -> {
                CompoundTag data = be.getCustomUpdateTag(level.registryAccess());
                UpdateBlockEntityPacket packet = new UpdateBlockEntityPacket(this.pos, data);
                ctx.reply(packet);
            });
        }
    }
}
