package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwitchConfiscationModePacket(BlockPos pos, InterdictionMatrix.ConfiscationMode mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SwitchConfiscationModePacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("switch_confiscation_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SwitchConfiscationModePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        SwitchConfiscationModePacket::pos,
        ModUtil.CONFISCATION_MODE_STREAM_CODEC,
        SwitchConfiscationModePacket::mode,
        SwitchConfiscationModePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
        Network.findBlockEntity(ModObjects.INTERDICTION_MATRIX_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setConfiscationMode(this.mode));
    }
}
