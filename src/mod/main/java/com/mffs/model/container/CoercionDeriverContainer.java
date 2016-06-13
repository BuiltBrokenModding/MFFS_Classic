package com.mffs.model.container;

import com.mffs.api.IPlayerUsing;
import com.mffs.api.slots.CardSlot;
import com.mffs.api.slots.MachineSlot;
import com.mffs.model.items.modules.upgrades.ModuleScale;
import com.mffs.model.tile.type.EntityCoercionDeriver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Calclavia
 */
public class CoercionDeriverContainer extends Container {

    public static int xInventoryDisplacement = 8;
    public static int yInventoryDisplacement = 135;
    public static int yHotBarDisplacement = 193;
    protected int slotCount = 0;
    private IInventory inventory;

    public CoercionDeriverContainer(EntityPlayer player, EntityCoercionDeriver driver) {
        this.inventory = driver;
        this.slotCount = inventory.getSizeInventory();
        if ((inventory instanceof IPlayerUsing)) {
            ((IPlayerUsing) inventory).getPlayersUsing().add(player);
        }
        //Add slots
        addSlotToContainer(new CardSlot<>(driver, 0, 9, 41));
        addSlotToContainer(new MachineSlot<>(driver, 1, 9, 83));
        addSlotToContainer(new MachineSlot<>(driver, 2, 29, 83));

        addSlotToContainer(new MachineSlot<>(driver, 3, 154, 67));
        addSlotToContainer(new MachineSlot<>(driver, 4, 154, 87));
        addSlotToContainer(new MachineSlot<>(driver, 5, 154, 47));
        addPlayerInventory(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer) {
        if ((this.inventory instanceof IPlayerUsing)) {
            ((IPlayerUsing) this.inventory).getPlayersUsing().remove(entityplayer);
        }
        super.onContainerClosed(entityplayer);
    }

    /**
     * @param player
     */
    public void addPlayerInventory(EntityPlayer player) {
        if ((this.inventory instanceof IPlayerUsing)) {
            ((IPlayerUsing) this.inventory).getPlayersUsing().add(player);
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, this.xInventoryDisplacement + x * 18, this.yInventoryDisplacement + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(player.inventory, x, this.xInventoryDisplacement + x * 18, this.yHotBarDisplacement));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotID) {
        ItemStack var2 = null;

        Slot var3 = (Slot) this.inventorySlots.get(slotID);
        if (var3 != null && var3.getHasStack()) {
            ItemStack itemStack = var3.getStack();
            var2 = itemStack.copy();
            if (slotID >= this.slotCount) {
                boolean didTry = false;
                for (int i = 0; i < this.slotCount; i++) {
                    if (getSlot(i).isItemValid(itemStack)) {
                        didTry = true;
                        if (mergeItemStack(itemStack, i, i + 1, false)) {
                            break;
                        }
                    }
                }
                if (!didTry) {
                    if (slotID < 27 + this.slotCount) {
                        if (!mergeItemStack(itemStack, 27 + this.slotCount, 36 + this.slotCount, false)) {
                            return null;
                        }
                    } else if ((slotID >= 27 + this.slotCount) && (slotID < 36 + this.slotCount) && (!mergeItemStack(itemStack, this.slotCount, 27 + this.slotCount, false))) {
                        return null;
                    }
                }
            } else if (!mergeItemStack(itemStack, this.slotCount, 36 + this.slotCount, false)) {
                return null;
            }
            if (itemStack.stackSize == 0) {
                var3.putStack(null);
            } else {
                var3.onSlotChanged();
            }
            if (itemStack.stackSize == var2.stackSize) {
                return null;
            }
            var3.onPickupFromSlot(par1EntityPlayer, itemStack);
        }
        return var2;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return this.inventory.isUseableByPlayer(entityplayer);
    }
}
