package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleFieldPermissionPacket(BlockPos pos, FieldPermission permission, boolean value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleFieldPermissionPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("toggle_field_permission"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleFieldPermissionPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ToggleFieldPermissionPacket::pos,
        ModUtil.FIELD_PERMISSION_STREAM_CODEC,
        ToggleFieldPermissionPacket::permission,
        ByteBufCodecs.BOOL,
        ToggleFieldPermissionPacket::value,
        ToggleFieldPermissionPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        Level level = ctx.player().level();
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
