package dev.su5ed.mffs.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SlotItemHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    private final Runnable onChanged;
    private final List<InventorySlot> slots = new ArrayList<>();
    
    public SlotItemHandler(Runnable onChanged) {
        this.onChanged = onChanged;
    }
    
    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter) {
        InventorySlot slot = new InventorySlot(this, name, mode, filter);
        this.slots.add(slot);
        return slot;
    }
    
    public void setChanged() {
        this.onChanged.run();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        validateSlotIndex(slot);
        this.slots.get(slot).setItem(stack);
    }

    @Override
    public int getSlots() {
        return this.slots.size();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.slots.get(slot).getItem();
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        validateSlotIndex(slot);
        InventorySlot invSlot = this.slots.get(slot);
        if (invSlot.canInsert(stack)) {
            if (!simulate) {
                invSlot.setItem(stack);
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);
        InventorySlot invSlot = this.slots.get(slot);
        if (invSlot.canExtract(amount)) {
            return invSlot.extract(amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        validateSlotIndex(slot);
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        validateSlotIndex(slot);
        return this.slots.get(slot).canInsert(stack);
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        this.slots.forEach(slot -> {
            CompoundTag tag = new CompoundTag();
            slot.getItem().save(tag);
            nbt.put(slot.getName(), tag);
        });
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.slots.forEach(slot -> {
            if (nbt.contains(slot.getName())) {
                CompoundTag tag = nbt.getCompound(slot.getName());
                slot.setItem(ItemStack.of(tag));
            }
        });
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.slots.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.slots.size() + ")");
    }
}
