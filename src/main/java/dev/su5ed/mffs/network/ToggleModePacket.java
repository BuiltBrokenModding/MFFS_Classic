package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.menu.FortronMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

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

    public void processServerPacket(NetworkEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        Network.findBlockEntity(Activatable.class, player.level(), this.pos)
            .ifPresent(be -> {
                be.setActive(this.active);
                if (player.containerMenu instanceof FortronMenu<?> fortronMenu) {
                    fortronMenu.triggerMenuAdvancement();
                }
            });
    }
}
