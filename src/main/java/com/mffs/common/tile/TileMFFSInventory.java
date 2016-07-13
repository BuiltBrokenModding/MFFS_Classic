package com.mffs.common.tile;

import com.mffs.api.utils.Util;
import com.mffs.common.TileMFFS;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public abstract class TileMFFSInventory extends TileMFFS implements IInventory {

    /* Inventory of this object */
    protected ItemStack[] inventory = new ItemStack[getSizeInventory()];

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    /**
     * Returns the stack in slot i
     *
     * @param slot
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     *
     * @param i
     * @param j
     */
    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (this.inventory[i] != null) {
            if (this.inventory[i].stackSize <= j) {
                ItemStack itemstack = this.inventory[i];
                this.inventory[i] = null;
                fireEvents(i);
                return itemstack;
            }
            ItemStack itemstack1 = this.inventory[i].splitStack(j);
            if (this.inventory[i].stackSize == 0) {
                this.inventory[i] = null;
            }
            fireEvents(i);
            return itemstack1;
        }
        return null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     *
     * @param p_70304_1_
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     *
     * @param slot
     * @param item
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack item) {
        this.inventory[slot] = item;
        if ((item != null) && (item.stackSize > getInventoryStackLimit())) {
            item.stackSize = getInventoryStackLimit();
        }
        fireEvents(slot);
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        if (getBlockType() != null) {
            return getBlockType().getLocalizedName();
        }
        return null;
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
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     *
     * @param p_70300_1_
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    /* Fires event when items have been changed */
    public void fireEvents(int... slots) {
    }

    public boolean mergeIntoInventory(ItemStack stack) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            stack = placeAdjInv(stack, dir);
            if (stack == null || stack.stackSize <= 0)
                return true;
        }
        return worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + .5, yCoord + 1, zCoord + .5, stack));
    }

    /**
     * @param stack
     * @param dir
     * @return
     */
    public ItemStack placeAdjInv(ItemStack stack, ForgeDirection dir) {
        TileEntity tileEntity = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
        //ForgeDirection o_dir = dir.getOpposite();
        if (stack != null && tileEntity != null) {
            if (tileEntity instanceof TileEntityChest) {
                TileEntityChest chest1 = (TileEntityChest) tileEntity;
                return Util.addToInv_first(chest1, stack);
            } else if (tileEntity instanceof ISidedInventory) {
                ISidedInventory inv = (ISidedInventory) tileEntity;
                int[] slot = inv.getAccessibleSlotsFromSide(dir.ordinal());
                for (int s : slot) {
                    if (inv.canInsertItem(s, stack, dir.ordinal())) {
                        stack = Util.addToInv_slot(inv, stack, s);
                        if (stack == null || stack.stackSize <= 0)
                            return null;
                    }
                }
            } else if (tileEntity instanceof IInventory) {
                IInventory inv = (IInventory) tileEntity;
                return Util.addToInv_first(inv, stack);
            }
        }
        return stack;
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
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("slot", (byte) i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbtTagList.appendTag(nbttagcompound1);
            }
        }
        nbt.setTag("items", nbtTagList);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbtTagList = nbt.getTagList("items", Constants.NBT.TAG_COMPOUND);
        this.inventory = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound1 = nbtTagList.getCompoundTagAt(i);

            byte byte0 = nbttagcompound1.getByte("slot");
            if ((byte0 >= 0) && (byte0 < this.inventory.length)) {
                this.inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    public Set<ItemStack> getCards() {
        Set<ItemStack> cards = new HashSet<>();
        cards.add(getStackInSlot(0));
        return cards;
    }


}
