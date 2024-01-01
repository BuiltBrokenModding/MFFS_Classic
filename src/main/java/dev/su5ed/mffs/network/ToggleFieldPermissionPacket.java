package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ToggleFieldPermissionPacket(BlockPos pos, FieldPermission permission, boolean value) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("toggle_field_permission");

    public ToggleFieldPermissionPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readEnum(FieldPermission.class), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.permission);
        buf.writeBoolean(this.value);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        Level level = ctx.player().orElseThrow().level();
        Network.findBlockEntity(BiometricIdentifierBlockEntity.class, level, this.pos)
            .flatMap(BiometricIdentifierBlockEntity::getManipulatingCard)
            .ifPresent(card -> {
                if (this.value) {
                    card.addPermission(this.permission);
                } else {
                    card.removePermission(this.permission);
                }
            });
    }
}
