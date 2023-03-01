package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ToggleFieldPermissionPacket(BlockPos pos, FieldPermission permission, boolean value) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.permission);
        buf.writeBoolean(this.value);
    }

    public static ToggleFieldPermissionPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        FieldPermission permission = buf.readEnum(FieldPermission.class);
        boolean enabled = buf.readBoolean();
        return new ToggleFieldPermissionPacket(pos, permission, enabled);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        Level level = ctx.get().getSender().getLevel();
        Network.findBlockEntity(BiometricIdentifierBlockEntity.class, level, this.pos)
            .flatMap(be -> be.rightsSlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD).resolve())
            .ifPresent(card -> {
                if (this.value) {
                    card.addPermission(this.permission);
                }
                else {
                    card.removePermission(this.permission);
                }
            });
    }
}
