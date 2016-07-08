package com.mffs.api.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by pwaln on 6/3/2016.
 */
public class MachineSlot<ENTITY extends IInventory> extends Slot {

    /* The IInventory that will be assigned. */
    protected ENTITY inventoryTile;

    /**
     * Constructor.
     *
     * @param entity The entity
     * @param slotId The slot id.
     * @param xPos   The Xpossition of the slot.
     * @param yPos   The Y position of the slot.
     */
    public MachineSlot(ENTITY entity, int slotId, int xPos, int yPos) {
        super(entity, slotId, xPos, yPos);
        this.inventoryTile = entity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return inventoryTile.isItemValidForSlot(this.slotNumber, stack);
    }

    public int getSlotStackLimit() {
        ItemStack stack = inventoryTile.getStackInSlot(this.slotNumber);
        if (stack != null) {
            return stack.getMaxStackSize();
        }
        return inventoryTile.getInventoryStackLimit();
    }
}
