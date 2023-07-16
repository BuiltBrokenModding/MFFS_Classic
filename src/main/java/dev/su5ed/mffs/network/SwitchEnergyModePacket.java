package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SwitchEnergyModePacket(BlockPos pos, CoercionDeriverBlockEntity.EnergyMode mode) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.mode);
    }

    public static SwitchEnergyModePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CoercionDeriverBlockEntity.EnergyMode mode = buf.readEnum(CoercionDeriverBlockEntity.EnergyMode.class);
        return new SwitchEnergyModePacket(pos, mode);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        Level level = ctx.get().getSender().level();
        Network.findBlockEntity(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setEnergyMode(this.mode));
    }
}
