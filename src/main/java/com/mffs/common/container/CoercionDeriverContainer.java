package com.mffs.common.container;

import com.mffs.api.slots.CardSlot;
import com.mffs.api.slots.MachineSlot;
import com.mffs.common.tile.type.TileCoercionDeriver;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Calclavia
 */
public class CoercionDeriverContainer extends PlayerContainer {

    /**
     * @param player
     * @param driver
     */
    public CoercionDeriverContainer(EntityPlayer player, TileCoercionDeriver driver) {
        super(player, driver);
        //Add slots
        addSlotToContainer(new CardSlot<>(driver, 0, 9, 41));
        addSlotToContainer(new MachineSlot<>(driver, 1, 9, 83));
        addSlotToContainer(new MachineSlot<>(driver, 2, 29, 83));

        addSlotToContainer(new MachineSlot<>(driver, 3, 154, 67));
        addSlotToContainer(new MachineSlot<>(driver, 4, 154, 87));
        addSlotToContainer(new MachineSlot<>(driver, 5, 154, 47));
        addPlayerInventory(player);
    }
}
