package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ToggleEnergyModePacket(BlockPos pos, CoercionDeriverBlockEntity.EnergyMode mode) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.mode);
    }

    public static ToggleEnergyModePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CoercionDeriverBlockEntity.EnergyMode mode = buf.readEnum(CoercionDeriverBlockEntity.EnergyMode.class);
        return new ToggleEnergyModePacket(pos, mode);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        Level level = ctx.get().getSender().getLevel();
        Network.findBlockEntity(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(this::process);
    }
    
    public void process(CoercionDeriverBlockEntity be) {
        be.setEnergyMode(this.mode);
    }
}
