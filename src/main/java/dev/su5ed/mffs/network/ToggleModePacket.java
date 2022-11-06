package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ToggleModePacket(BlockPos pos, boolean active) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.active);
    }

    public static ToggleModePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean enabled = buf.readBoolean();
        return new ToggleModePacket(pos, enabled);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        Level level = ctx.get().getSender().getLevel();
        Network.findBlockEntity(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(this::process);
    }
    
    public void process(CoercionDeriverBlockEntity be) {
        be.setActive(this.active);
    }
}
