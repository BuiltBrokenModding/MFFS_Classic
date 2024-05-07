package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.menu.FortronMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleModePacket(BlockPos pos, boolean active) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleModePacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("toggle_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleModePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ToggleModePacket::pos,
        ByteBufCodecs.BOOL,
        ToggleModePacket::active,
        ToggleModePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Player player = ctx.player();
        Network.findBlockEntity(Activatable.class, player.level(), this.pos)
            .ifPresent(be -> {
                be.setActive(this.active);
                if (player.containerMenu instanceof FortronMenu<?> fortronMenu) {
                    fortronMenu.triggerMenuAdvancement();
                }
            });
    }
}
