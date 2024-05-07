package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateFrequencyPacket(BlockPos pos, int frequency) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateFrequencyPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("update_frequency"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFrequencyPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        UpdateFrequencyPacket::pos,
        ByteBufCodecs.INT,
        UpdateFrequencyPacket::frequency,
        UpdateFrequencyPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
        Network.findBlockEntity(ModCapabilities.FORTRON, level, this.pos)
            .ifPresent(be -> be.setFrequency(this.frequency));
    }
}
