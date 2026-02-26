package dev.su5ed.mffs.util.inventory;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventorySlotItemHandler implements ResourceHandler<ItemResource>, ValueIOSerializable {
    private final Runnable onChanged;
    private final List<InventorySlot> slots = new ArrayList<>();
    private final ArrayList<InventorySlotJournal> snapshotJournals = new ArrayList<>();

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
        this.snapshotJournals.add(new InventorySlotJournal(this.slots.size() - 1));
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

    protected int getCapacity(ItemResource resource) {
        return resource.isEmpty() ? Item.ABSOLUTE_MAX_STACK_SIZE : Math.min(resource.getMaxStackSize(), Item.ABSOLUTE_MAX_STACK_SIZE);
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

    @Override
    public int size() {
        return this.slots.size();
    }

    @Override
    public ItemResource getResource(int index) {
        Objects.checkIndex(index, size());
        return ItemResource.of(this.slots.get(index).getItem());
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return this.slots.get(index).getItem().getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        Objects.checkIndex(index, size());
        return resource.isEmpty() || isValid(index, resource) ? getCapacity(resource) : 0;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        Objects.checkIndex(index, size());
        return this.slots.get(index).accepts(resource.toStack());
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        ItemStack currentStack = slots.get(index).getItem();
        int currentAmount = currentStack.getCount();

        if ((currentAmount == 0 || ItemResource.of(currentStack).equals(resource)) && isValid(index, resource)) {
            int inserted = Math.min(amount, getCapacity(resource) - currentAmount);

            if (inserted > 0) {
                snapshotJournals.get(index).updateSnapshots(transaction);
                slots.get(index).setItem(resource.toStack(currentAmount + inserted), false);
                return inserted;
            }
        }

        return 0;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        ItemStack currentStack = slots.get(index).getItem();

        if (ItemResource.of(currentStack).equals(resource)) {
            int currentAmount = currentStack.getCount();
            int extracted = Math.min(amount, currentAmount);

            if (extracted > 0) {
                snapshotJournals.get(index).updateSnapshots(transaction);
                slots.get(index).setItem(resource.toStack(currentAmount - extracted), false);
                return extracted;
            }
        }

        return 0;
    }

    private class InventorySlotJournal extends SnapshotJournal<ItemStack> {
        private final int index;

        private InventorySlotJournal(int index) {
            this.index = index;
        }

        @Override
        protected ItemStack createSnapshot() {
            return slots.get(index).getItem().copy();
        }

        @Override
        protected void revertToSnapshot(ItemStack snapshot) {
            slots.get(index).setItem(snapshot);
        }

        @Override
        protected void onRootCommit(ItemStack originalState) {
            slots.get(index).onChanged(true);
        }
    }
}
