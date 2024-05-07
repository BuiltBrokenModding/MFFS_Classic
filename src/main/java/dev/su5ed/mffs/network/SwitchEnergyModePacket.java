package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwitchEnergyModePacket(BlockPos pos, CoercionDeriverBlockEntity.EnergyMode mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SwitchEnergyModePacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("switch_energy_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SwitchEnergyModePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        SwitchEnergyModePacket::pos,
        CoercionDeriverBlockEntity.EnergyMode.STREAM_CODEC,
        SwitchEnergyModePacket::mode,
        SwitchEnergyModePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
        Network.findBlockEntity(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setEnergyMode(this.mode));
    }
}
