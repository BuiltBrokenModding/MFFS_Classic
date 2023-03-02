package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.inventory.SlotActive;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import one.util.streamex.EntryStream;

import java.util.HashSet;
import java.util.Set;

public class BiometricIdentifierMenu extends FortronMenu<BiometricIdentifierBlockEntity> {
    private final Set<FieldPermission> fieldPermissions = new HashSet<>();

    public BiometricIdentifierMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.BIOMETRIC_IDENTIFIER_MENU.get(), ModObjects.BIOMETRIC_IDENTIFIER_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);

        addInventorySlot(new SlotActive(this.blockEntity.frequencySlot, 88, 91, this.blockEntity));
        addInventorySlot(new SlotInventory(this.blockEntity.rightsSlot, 8, 46));
        addInventorySlot(new SlotInventory(this.blockEntity.copySlot, 8, 66));
        addInventorySlot(new SlotActive(this.blockEntity.masterSlot, 8, 91, this.blockEntity));
        EntryStream.of(this.blockEntity.identitySlots)
            .forKeyValue((i, slot) -> addSlot(new SlotActive(slot, 8 + i * 18, 111, this.blockEntity)));

        addDataSlot(
            () -> this.blockEntity.getManipulatingCard()
                .map(card -> {
                    int perms = 0;
                    for (int i = 0; i < FieldPermission.values().length; i++) {
                        perms <<= 1;
                        int value = card.hasPermission(FieldPermission.values()[i]) ? 1 : 0;
                        perms |= value;
                    }
                    return perms;
                })
                .orElse(0),
            perms -> {
                this.fieldPermissions.clear();
                for (int i = FieldPermission.values().length - 1; i >= 0; i--) {
                    if ((perms & 1) == 1) {
                        this.fieldPermissions.add(FieldPermission.values()[i]);
                    }
                    perms >>= 1;
                }
            }
        );
    }

    public boolean hasPermission(FieldPermission permission) {
        return fieldPermissions.contains(permission);
    }
}
