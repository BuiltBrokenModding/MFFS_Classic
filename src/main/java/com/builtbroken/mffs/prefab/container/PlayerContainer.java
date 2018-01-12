package com.builtbroken.mffs.prefab.container;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Calclavia
 */
@Deprecated
public class PlayerContainer<H extends Object> extends ContainerBase<H>
{
    public PlayerContainer(EntityPlayer player, H inventory)
    {
        super(player, inventory);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotID)
    {
        ItemStack var2 = null;

        Slot var3 = (Slot) this.inventorySlots.get(slotID);
        if (var3 != null && var3.getHasStack())
        {
            ItemStack itemStack = var3.getStack();
            var2 = itemStack.copy();
            if (slotID >= this.slotCount)
            {
                boolean didTry = false;
                for (int i = 0; i < this.slotCount; i++)
                {
                    if (getSlot(i).isItemValid(itemStack))
                    {
                        didTry = true;
                        if (mergeItemStack(itemStack, i, i + 1, false))
                        {
                            break;
                        }
                    }
                }
                if (!didTry)
                {
                    if (slotID < 27 + this.slotCount)
                    {
                        if (!mergeItemStack(itemStack, 27 + this.slotCount, 36 + this.slotCount, false))
                        {
                            return null;
                        }
                    }
                    else if ((slotID >= 27 + this.slotCount) && (slotID < 36 + this.slotCount) && (!mergeItemStack(itemStack, this.slotCount, 27 + this.slotCount, false)))
                    {
                        return null;
                    }
                }
            }
            else if (!mergeItemStack(itemStack, this.slotCount, 36 + this.slotCount, false))
            {
                return null;
            }
            if (itemStack.stackSize == 0)
            {
                var3.putStack(null);
            }
            else
            {
                var3.onSlotChanged();
            }
            if (itemStack.stackSize == var2.stackSize)
            {
                return null;
            }
            var3.onPickupFromSlot(par1EntityPlayer, itemStack);
        }
        return var2;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return this.inventory.isUseableByPlayer(entityplayer);
    }
}
