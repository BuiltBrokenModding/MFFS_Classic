package com.mffs.common.container;

import com.mffs.api.slots.ActiveSlot;
import com.mffs.api.slots.CardSlot;
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

        addSlotToContainer(new CardSlot<>(bio, 0, 8, 114));


        for (int x = 0; x < 9; x++)
            for(int y = 0; y < 4; y++)
            addSlotToContainer(new CardSlot<>(bio, x + y * 9 + 1, 9 + x * 18, 36 + y * 18));

        addPlayerInventory(player);
    }
}
