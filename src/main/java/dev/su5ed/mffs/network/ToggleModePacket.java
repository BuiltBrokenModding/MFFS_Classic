package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.menu.FortronMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ToggleModePacket(BlockPos pos, boolean active) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("toggle_mode");

    public ToggleModePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.active);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        Player player = ctx.player().orElseThrow();
        Network.findBlockEntity(Activatable.class, player.level(), this.pos)
            .ifPresent(be -> {
                be.setActive(this.active);
                if (player.containerMenu instanceof FortronMenu<?> fortronMenu) {
                    fortronMenu.triggerMenuAdvancement();
                }
            });
    }
}
