package dev.su5ed.mffs.util.inventory;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InventorySlot {
    private final InventorySlotItemHandler parent;
    private final String name;
    private final Mode mode;
    private final Predicate<ItemStack> filter;
    private final Consumer<ItemStack> onChanged;
    private final boolean virtual;
    /** Optional per-slot capacity override.  Given the incoming {@link ItemStack}, returns the
     *  maximum number of that item this slot may hold in total (across stacking).  When {@code null},
     *  the item's own {@code getMaxStackSize()} is used as the limit. */
    @Nullable
    private final Function<ItemStack, Integer> capacityProvider;

    private ItemStack content = ItemStack.EMPTY;
    private ItemStack lastNotifiedContent = ItemStack.EMPTY;
    // Snapshot of lastNotifiedContent immediately before the most-recent onChange notification.
    // Accessible from the onChange callback (via getPreviousItem()) so callers can compare
    // old vs new content without requiring a BiConsumer callback signature.
    private ItemStack previousContent = ItemStack.EMPTY;

    public InventorySlot(InventorySlotItemHandler parent, String name, Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged, boolean virtual) {
        this(parent, name, mode, filter, onChanged, virtual, null);
    }

    public InventorySlot(InventorySlotItemHandler parent, String name, Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged, boolean virtual, @Nullable Function<ItemStack, Integer> capacityProvider) {
        this.parent = parent;
        this.name = name;
        this.mode = mode;
        this.filter = filter;
        this.onChanged = onChanged;
        this.virtual = virtual;
        this.capacityProvider = capacityProvider;
    }

    public String getName() {
        return this.name;
    }

    public boolean canInsert(ItemStack stack) {
        return this.mode.input && accepts(stack);
    }

    public boolean canExtract() {
        return this.mode.output;
    }

    public boolean isEmpty() {
        return this.content.isEmpty();
    }

    public ItemStack getItem() {
        return this.content;
    }

    /**
     * Returns the slot's content as it was immediately before the most-recent
     * {@link #onChanged} notification was fired.  Only valid to call from inside an
     * {@code onChanged} callback; outside of a callback it may reflect stale state.
     */
    public ItemStack getPreviousItem() {
        return this.previousContent;
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    public void setItem(ItemStack stack) {
        setItem(stack, true);
    }

    public void setItem(ItemStack stack, boolean notify) {
        this.content = stack;
        if (notify) {
            onChanged(true);
        } else {
            // Silent set (e.g. NBT load) — sync snapshot to avoid spurious fire later
            this.lastNotifiedContent = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (!stack.isEmpty() && canAdd(stack)) {
            if (this.capacityProvider == null) {
                // --- Default path (unchanged behaviour) ---
                if (this.content.isEmpty()) {
                    if (!simulate) {
                        setItem(stack);
                    }
                    return ItemStack.EMPTY;
                }
                if (!simulate) {
                    int total = Math.min(this.content.getCount() + stack.getCount(), this.content.getMaxStackSize());
                    this.content.setCount(total);
                }
                int remainder = this.content.getCount() + stack.getCount() - this.content.getMaxStackSize();
                ItemStack result;
                if (remainder > 0) {
                    result = stack.copy();
                    result.setCount(remainder);
                } else {
                    result = ItemStack.EMPTY;
                }
                onChanged(true);
                return result;
            } else {
                // --- Capacity-limited path ---
                int limit = Math.max(0, this.capacityProvider.apply(stack));
                if (limit <= 0) return stack;
                int currentCount = this.content.isEmpty() ? 0 : this.content.getCount();
                int canAccept = Math.min(stack.getCount(), Math.max(0, limit - currentCount));
                if (canAccept <= 0) return stack;
                if (!simulate) {
                    if (this.content.isEmpty()) {
                        ItemStack toSet = stack.copy();
                        toSet.setCount(canAccept);
                        setItem(toSet);
                    } else {
                        this.content.setCount(currentCount + canAccept);
                        onChanged(true);
                    }
                }
                if (canAccept >= stack.getCount()) return ItemStack.EMPTY;
                ItemStack remainder = stack.copy();
                remainder.setCount(stack.getCount() - canAccept);
                return remainder;
            }
        }
        return stack;
    }

    public boolean accepts(ItemStack stack) {
        return this.filter.test(stack);
    }

    /**
     * Returns the maximum number of {@code stack} that this slot may hold in total.
     * When no capacity provider is configured this is simply {@code stack.getMaxStackSize()}.
     */
    public int getCapacityFor(ItemStack stack) {
        if (this.capacityProvider == null) return stack.getMaxStackSize();
        return Math.max(0, this.capacityProvider.apply(stack));
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (canExtract()) {
            if (!simulate) {
                ItemStack stack = this.content.splitStack(amount);
                onChanged(true);
                return stack;
            }
            ItemStack copy = this.content.copy();
            copy.setCount(Math.min(amount, this.content.getCount()));
            return copy;
        }
        return ItemStack.EMPTY;
    }

    protected void onChanged(boolean notify) {
        if (notify) {
            ItemStack current = getItem();
            boolean same = (this.lastNotifiedContent.isEmpty() && current.isEmpty())
                || (!this.lastNotifiedContent.isEmpty() && !current.isEmpty()
                    && ItemStack.areItemsEqual(this.lastNotifiedContent, current)
                    && ItemStack.areItemStackTagsEqual(this.lastNotifiedContent, current)
                    && this.lastNotifiedContent.getCount() == current.getCount());
            if (!same) {
                this.previousContent = this.lastNotifiedContent;
                this.lastNotifiedContent = current.isEmpty() ? ItemStack.EMPTY : current.copy();
                this.parent.onChanged();
                this.onChanged.accept(current);
            }
        }
    }

    private boolean canAdd(ItemStack stack) {
        return accepts(stack) && (this.content.isEmpty()
            || (ItemStack.areItemsEqual(this.content, stack)
                && ItemStack.areItemStackTagsEqual(this.content, stack)));
    }

    public enum Mode {
        INPUT(true, false),
        OUTPUT(false, true),
        BOTH(true, true),
        NONE(false, false);

        private final boolean input;
        private final boolean output;

        Mode(boolean input, boolean output) {
            this.input = input;
            this.output = output;
        }
    }
}
