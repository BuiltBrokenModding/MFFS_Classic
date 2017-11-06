package com.builtbroken.mffs.content.gen.gui;

import com.builtbroken.mc.prefab.gui.slot.SlotEnergyItem;
import com.builtbroken.mffs.content.gen.TileCoercionDeriver;
import com.builtbroken.mffs.prefab.container.PlayerContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @author Calclavia
 */
public class ContainerCoercionDeriver extends PlayerContainer
{

    /**
     * @param player
     * @param driver
     */
    public ContainerCoercionDeriver(EntityPlayer player, TileCoercionDeriver driver, int id)
    {
        super(player, driver);
        if (id == TileCoercionDeriver.GUI_MAIN)
        {
            //Add slots
            for (int i = 0; i < 4; i++)
            {
                addSlotToContainer(new SlotEnergyItem(driver, i, 9 + 9 * i, 41));
            }
            addSlotToContainer(new Slot(driver, TileCoercionDeriver.SLOT_FUEL, 29, 83));
        }
        else if (id == TileCoercionDeriver.GUI_UPGRADES)
        {
            //Upgrades
            addSlotToContainer(new Slot(driver, 3, 154, 67));
            addSlotToContainer(new Slot(driver, 4, 154, 87));
            addSlotToContainer(new Slot(driver, 5, 154, 47));
        }

        //Player inventory
        addPlayerInventory(player);
    }
}
