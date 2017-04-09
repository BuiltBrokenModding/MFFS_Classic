package com.mffs.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
public class HotBarContainer extends Container
{
    /**
     * @param player
     */
    public HotBarContainer(EntityPlayer player)
    {
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(player.inventory, x, PlayerContainer.xInventoryDisplacement + x * 18, PlayerContainer.yHotBarDisplacement));
        }
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     *
     * @param p_82846_1_
     * @param p_82846_2_
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        return null;
    }

    /**
     * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to check if
     * the slot can be added to a list of Slots to split the held ItemStack across.
     *
     * @param p_94531_1_
     */
    @Override
    public boolean canDragIntoSlot(Slot p_94531_1_)
    {
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }
}
