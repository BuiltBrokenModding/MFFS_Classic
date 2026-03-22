package dev.su5ed.mffs.util.inventory;

import dev.su5ed.mffs.util.TooltipSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SlotInventory extends Slot implements TooltipSlot {

    /** A no-op inventory placeholder — all real I/O is delegated to InventorySlot. */
    private static final class EmptyInventory implements IInventory {
        static final EmptyInventory INSTANCE = new EmptyInventory();
        @Override public int getSizeInventory() { return 0; }
        @Override public boolean isEmpty() { return true; }
        @Override public ItemStack getStackInSlot(int i) { return ItemStack.EMPTY; }
        @Override public ItemStack decrStackSize(int i, int n) { return ItemStack.EMPTY; }
        @Override public ItemStack removeStackFromSlot(int i) { return ItemStack.EMPTY; }
        @Override public void setInventorySlotContents(int i, ItemStack s) {}
        @Override public int getInventoryStackLimit() { return 64; }
        @Override public void markDirty() {}
        @Override public boolean isUsableByPlayer(EntityPlayer p) { return true; }
        @Override public void openInventory(EntityPlayer p) {}
        @Override public void closeInventory(EntityPlayer p) {}
        @Override public boolean isItemValidForSlot(int i, ItemStack s) { return false; }
        @Override public int getField(int i) { return 0; }
        @Override public void setField(int i, int v) {}
        @Override public int getFieldCount() { return 0; }
        @Override public void clear() {}
        @Override public String getName() { return ""; }
        @Override public boolean hasCustomName() { return false; }
        @Override public ITextComponent getDisplayName() { return new TextComponentString(""); }
    }

    private final InventorySlot inventorySlot;
    @Nullable
    private final ITextComponent tooltip;
    // Tracks the last item tested by isItemValid so getSlotStackLimit() can return the
    // correct capacity for empty slots (vanilla mergeItemStack calls the no-arg variant).
    @Nullable
    private ItemStack lastValidatedItem;

    public SlotInventory(InventorySlot inventorySlot, int x, int y) {
        this(inventorySlot, x, y, null);
    }

    public SlotInventory(InventorySlot inventorySlot, int x, int y, @Nullable ITextComponent tooltip) {
        super(EmptyInventory.INSTANCE, -1, x, y);
        this.inventorySlot = inventorySlot;
        this.tooltip = tooltip;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        this.inventorySlot.onChanged(true);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.isEmpty() || !this.inventorySlot.canInsert(stack)) return false;
        this.lastValidatedItem = stack;
        // Check that there is at least one unit of remaining capacity for this item.
        ItemStack current = this.inventorySlot.getItem();
        int currentCount = (!current.isEmpty() && ItemStack.areItemsEqual(current, stack)
            && ItemStack.areItemStackTagsEqual(current, stack)) ? current.getCount() : 0;
        return this.inventorySlot.getCapacityFor(stack) > currentCount;
    }

    @Override
    public ItemStack getStack() {
        return this.inventorySlot.getItem();
    }

    @Override
    public void putStack(ItemStack stack) {
        if (!stack.isEmpty()) {
            // Safety cap: vanilla mergeItemStack calls putStack directly without going through
            // insert(), so enforce the capacity limit here as a last line of defence.
            int limit = this.inventorySlot.getCapacityFor(stack);
            if (stack.getCount() > limit) {
                stack = stack.copy();
                stack.setCount(limit);
            }
        }
        this.inventorySlot.setItem(stack);
        // onSlotChanged() intentionally omitted — setItem() already notifies via onChanged
    }

    @Override
    public void onSlotChange(ItemStack oldStack, ItemStack newStack) {}

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return this.inventorySlot.getCapacityFor(stack);
    }

    @Override
    public int getSlotStackLimit() {
        ItemStack current = this.inventorySlot.getItem();
        if (!current.isEmpty()) {
            return this.inventorySlot.getCapacityFor(current);
        }
        // For empty slots, use the item from the most recent isItemValid call so that
        // vanilla mergeItemStack (which calls the no-arg form) gets the correct limit.
        if (this.lastValidatedItem != null && !this.lastValidatedItem.isEmpty()) {
            return this.inventorySlot.getCapacityFor(this.lastValidatedItem);
        }
        return 64;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return this.inventorySlot.canExtract();
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return this.inventorySlot.extract(amount, false);
    }

    @Override
    public List<ITextComponent> getTooltips() {
        return this.tooltip != null ? Collections.singletonList(this.tooltip) : Collections.emptyList();
    }
}
