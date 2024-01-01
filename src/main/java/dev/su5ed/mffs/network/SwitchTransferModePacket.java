package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.TransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SwitchTransferModePacket(BlockPos pos, TransferMode mode) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("switch_transfer_mode");

    public SwitchTransferModePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readEnum(TransferMode.class));
    }

    public void handle(PlayPayloadContext ctx) {
        Level level = ctx.player().orElseThrow().level();
        Network.findBlockEntity(ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setTransferMode(this.mode));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.mode);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
