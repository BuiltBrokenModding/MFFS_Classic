package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.TransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwitchTransferModePacket(BlockPos pos, TransferMode mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SwitchTransferModePacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("switch_transfer_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SwitchTransferModePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        SwitchTransferModePacket::pos,
        TransferMode.STREAM_CODEC,
        SwitchTransferModePacket::mode,
        SwitchTransferModePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
        Network.findBlockEntity(ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setTransferMode(this.mode));
    }
}
