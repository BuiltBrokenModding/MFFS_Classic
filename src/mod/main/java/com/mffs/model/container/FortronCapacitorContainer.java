package com.mffs.model.container;

import com.mffs.api.slots.CardSlot;
import com.mffs.api.slots.MachineSlot;
import com.mffs.model.tile.type.TileFortronCapacitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * @author Calclavia
 */
public final class FortronCapacitorContainer extends PlayerContainer {

    /**
     *
     * @param player
     * @param cap
     */
    public FortronCapacitorContainer(EntityPlayer player, TileFortronCapacitor cap) {
        super(player, cap);

        addSlotToContainer(new CardSlot<>(cap, 0, 9, 74));
        addSlotToContainer(new CardSlot<>(cap, 1, 27, 74));

        addSlotToContainer(new MachineSlot<>(cap, 2, 1, 47));
        addSlotToContainer(new MachineSlot<>(cap, 3, 154, 67));
        addSlotToContainer(new MachineSlot<>(cap, 4, 154, 87));

        addPlayerInventory(player);
    }

}
