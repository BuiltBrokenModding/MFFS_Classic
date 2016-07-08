package com.mffs.api.slots;

import com.mffs.api.IActivatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by pwaln on 6/3/2016.
 */
public class ActiveSlot<ENTITY extends IInventory> extends MachineSlot {

    /**
     * Constructor.
     *
     * @param entity The entity
     * @param slotId The slot id.
     * @param xPos   The Xpossition of the slot.
     * @param yPos   The Y position of the slot.
     */
    public ActiveSlot(ENTITY entity, int slotId, int xPos, int yPos) {
        super(entity, slotId, xPos, yPos);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return super.isItemValid(stack)
                && inventoryTile instanceof IActivatable && ((IActivatable) inventoryTile).isActive();
    }

    @Override
    public boolean canTakeStack(EntityPlayer pl) {
        if (!(inventoryTile instanceof IActivatable)) {
            return true;
        }
        return ((IActivatable) inventoryTile).isActive();
    }
}
