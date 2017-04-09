package com.mffs.common.items.card.id;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot used to show a real item but can not be removed
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/9/2017.
 */
public class SlotShow extends Slot
{
    public SlotShow(IInventory inv, int slotID, int x, int y)
    {
        super(inv, slotID, x, y);
    }

    @Override
    public boolean canTakeStack(EntityPlayer p_82869_1_)
    {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack p_75214_1_)
    {
        return false;
    }
}
