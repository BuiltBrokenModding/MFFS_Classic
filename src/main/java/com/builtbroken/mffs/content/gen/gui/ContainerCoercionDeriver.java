package com.builtbroken.mffs.content.gen.gui;

import com.builtbroken.mc.prefab.gui.slot.SlotEnergyItem;
import com.builtbroken.mffs.content.gen.TileCoercionDeriver;
import com.builtbroken.mffs.prefab.container.PlayerContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @author Calclavia
 */
public class ContainerCoercionDeriver extends PlayerContainer<TileCoercionDeriver>
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
                addSlotToContainer(new SlotEnergyItem(driver, TileCoercionDeriver.SLOT_BATTERY_START + i, 150, 20 + 18 * i));
            }
            addSlotToContainer(new SlotCoercionFuel(driver, TileCoercionDeriver.SLOT_FUEL, 29, 83));
        }
        else if (id == TileCoercionDeriver.GUI_UPGRADES)
        {
            //Upgrades
            int x = 20;
            int y = 40;
            addSlotToContainer(new Slot(driver, TileCoercionDeriver.UPGRADES_START, x, y));
            addSlotToContainer(new Slot(driver, TileCoercionDeriver.UPGRADES_START + 1, x, y + 30));
            addSlotToContainer(new Slot(driver, TileCoercionDeriver.UPGRADES_START + 2, x, y + 60));
        }

        //Player inventory
        addPlayerInventory(player, 8 , 135);
    }
}
