package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SwitchConfiscationModePacket(BlockPos pos, InterdictionMatrix.ConfiscationMode mode) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("switch_confiscation_mode");

    public SwitchConfiscationModePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readEnum(InterdictionMatrix.ConfiscationMode.class));
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

    public void handle(PlayPayloadContext ctx) {
        Level level = ctx.player().orElseThrow().level();
        Network.findBlockEntity(ModObjects.INTERDICTION_MATRIX_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setConfiscationMode(this.mode));
    }
}
