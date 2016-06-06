package com.mffs.api.slots;

import com.mffs.api.IBlockFrequency;
import com.mffs.api.IItemFrequency;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by pwaln on 6/3/2016.
 */
public class CardSlot<ENTITY extends IInventory> extends MachineSlot {

    /**
     * Constructor.
     *
     * @param entity The entity
     * @param slotId The slot id.
     * @param xPos   The Xpossition of the slot.
     * @param yPos   The Y position of the slot.
     */
    public CardSlot(ENTITY entity, int slotId, int xPos, int yPos) {
        super(entity, slotId, xPos, yPos);
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        ItemStack stack = getStack();
        if (stack != null && stack.getItem() instanceof IItemFrequency
                && inventoryTile instanceof IBlockFrequency) {
            ((IItemFrequency) stack.getItem()).setFrequency(((IBlockFrequency) inventoryTile).getFrequency(), stack);
        }
    }
}
