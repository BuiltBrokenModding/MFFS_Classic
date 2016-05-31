package com.mffs.common.entity;

import com.mffs.MFFS;
import com.mffs.api.SecurityClearance;
import com.mffs.common.RegisterManager;
import com.mffs.common.items.card.AccessCard;
import com.mffs.common.items.card.PersonalIDCard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

/**
 * Created by pwaln on 5/30/2016.
 */
public class EntityAdvSecurityStation extends EntityMachine {

    /* The main user of this entity */
    private String mainUser;

    /* The inventory of this item */
    private ItemStack[] inventory = new ItemStack[40];

    /* If the security has been added */
    private boolean securityEnabled;

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     *
     * @param side
     */
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[0];
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     *
     * @param slot
     * @param stack
     * @param side
     */
    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return false;
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     *
     * @param slot
     * @param stack
     * @param side
     */
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    /**
     * Returns the stack in slot i
     *
     * @param slot
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     *
     * @param slot
     * @param amount
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.inventory[slot] != null) {
            if (this.inventory[slot].stackSize <= amount) {
                ItemStack itemstack = this.inventory[slot];
                this.inventory[slot] = null;
                return itemstack;
            }
            ItemStack itemstack1 = this.inventory[slot].splitStack(amount);
            if (this.inventory[slot].stackSize == 0) {
                this.inventory[slot] = null;
            }
       return itemstack1;
     }
     return null;
   }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     *
     * @param p_70299_1_
     * @param p_70299_2_
     */
    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {

    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return "Secstation";
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param p_94041_1_
     * @param p_94041_2_
     */
    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }

    @Override
    public boolean canAccess(String name, TileEntity tile, EntityPlayer player, SecurityClearance right) {
        if(!this.isOn) {
            return true;
        }
        if(mainUser.equalsIgnoreCase(name)) {
            return true;
        }
        //Check the machines inventory
        for(int i = 0; i < inventory.length; i++) {
            if(inventory[i].getItem() instanceof AccessCard) {
                NBTTagCompound tag = RegisterManager.getTag(inventory[i]);
                String user = tag.getString("name");
                if(name.equalsIgnoreCase(user)) {
                    return tag.getCompoundTag("rights").getBoolean(right.name());
                }
            }
        }
        //Check player inventory
        List<Slot> slots = player.inventoryContainer.inventorySlots;
        for(Slot slot : slots) {
            ItemStack item = slot.getStack();
            if(item != null && item.getItem() instanceof AccessCard) {
                NBTTagCompound nbt = RegisterManager.getTag(item);
                if(nbt.getInteger("validity") > 0) {
                    if(nbt.getInteger("linkID") == this.id
                            && nbt.getCompoundTag("rights").getBoolean(right.name())) {
                        if(!nbt.getString("Areaname").equalsIgnoreCase(this.name)) {
                            nbt.setString("Areaname", this.name);
                        }
                        return true;
                    }
                }
                else {
                   // slot.putStack(new ItemStack((EmptyCard) Item.itemRegistry.getObject(MFFS.MODID+":EmptyCard"), 1));
                }
            }

        }
        return false;
    }
}
