package dev.su5ed.mffs.util.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventorySlotItemHandler implements IItemHandler, IItemHandlerModifiable, ValueIOSerializable {
    private final Runnable onChanged;
    private final List<InventorySlot> slots = new ArrayList<>();

    public InventorySlotItemHandler(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter) {
        return addSlot(name, mode, filter, stack -> {
        });
    }

    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged) {
        return addSlot(name, mode, filter, onChanged, false);
    }

    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged, boolean virtual) {
        InventorySlot slot = new InventorySlot(this, name, mode, filter, onChanged, virtual);
        this.slots.add(slot);
        return slot;
    }

    public void onChanged() {
        this.onChanged.run();
    }

    public Collection<ItemStack> getAllItems() {
        return StreamEx.of(this.slots)
            .remove(InventorySlot::isVirtual)
            .map(InventorySlot::getItem)
            .remove(ItemStack::isEmpty)
            .toList();
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
        return this.slots.get(slot).insert(stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);
        return this.slots.get(slot).extract(amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        validateSlotIndex(slot);
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        validateSlotIndex(slot);
        return this.slots.get(slot).accepts(stack);
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.slots.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.slots.size() + ")");
    }

    @Override
    public void serialize(ValueOutput valueOutput) {
        ValueOutput slots = valueOutput.child("slots");
        this.slots.forEach(slot -> slots.putChild(slot.getName(), slot));
    }

    @Override
    public void deserialize(ValueInput valueInput) {
        ValueInput slots = valueInput.childOrEmpty("slots");
        this.slots.forEach(slot -> slots.child(slot.getName()).ifPresent(slot::deserialize));
    }
}
