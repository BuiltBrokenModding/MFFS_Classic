package com.mffs.common.container;

import com.mffs.api.slots.ActiveSlot;
import com.mffs.api.slots.MachineSlot;
import com.mffs.common.tile.type.TileBiometricIdentifier;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public class BiometricContainer extends PlayerContainer {

    /**
     * @param player
     * @param bio
     */
    public BiometricContainer(EntityPlayer player, TileBiometricIdentifier bio) {
        super(player, bio);

        addSlotToContainer(new ActiveSlot<>(bio, 0, 88, 91));
        addSlotToContainer(new MachineSlot<>(bio, 1, 8, 46));
        addSlotToContainer(new ActiveSlot<>(bio, 2, 8, 91));

        for (int slot = 0; slot < 9; slot++) {
            addSlotToContainer(new ActiveSlot<>(bio, 3 + slot, 8 + slot * 18, 111));
        }

        addSlotToContainer(new MachineSlot<>(bio, 12, 8, 66));
        addPlayerInventory(player);
    }
}
