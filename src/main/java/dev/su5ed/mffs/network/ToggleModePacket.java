package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.Activatable;
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
        Network.findBlockEntity(Activatable.class, level, this.pos)
            .ifPresent(be -> be.setActive(this.active));
    }
}
