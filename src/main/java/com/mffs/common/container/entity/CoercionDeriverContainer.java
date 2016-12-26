package com.mffs.common.container.entity;

import com.builtbroken.mc.prefab.gui.slot.SlotSpecific;
import com.mffs.api.slots.BaseSlot;
import com.mffs.common.container.PlayerContainer;
import com.mffs.common.items.card.ItemCardFrequency;
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
        addSlotToContainer(new SlotSpecific(driver, 0, 9, 41, ItemCardFrequency.class));
        addSlotToContainer(new BaseSlot(driver, 1, 9, 83));
        addSlotToContainer(new BaseSlot(driver, 2, 29, 83));

        addSlotToContainer(new BaseSlot(driver, 3, 154, 67));
        addSlotToContainer(new BaseSlot(driver, 4, 154, 87));
        addSlotToContainer(new BaseSlot(driver, 5, 154, 47));
        addPlayerInventory(player);
    }
}
