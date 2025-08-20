package dev.su5ed.mffs.util.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventorySlot implements ValueIOSerializable {
    private final InventorySlotItemHandler parent;
    private final String name;
    private final Mode mode;
    private final Predicate<ItemStack> filter;
    private final Consumer<ItemStack> onChanged;
    private final boolean virtual;

    private ItemStack content = ItemStack.EMPTY;

    public InventorySlot(InventorySlotItemHandler parent, String name, Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged, boolean virtual) {
        this.parent = parent;
        this.name = name;
        this.mode = mode;
        this.filter = filter;
        this.onChanged = onChanged;
        this.virtual = virtual;
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

    public boolean isVirtual() {
        return this.virtual;
    }

    public void setItem(ItemStack stack) {
        setItem(stack, true);
    }

    public void setItem(ItemStack stack, boolean notify) {
        this.content = stack;
        onChanged(notify);
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (!stack.isEmpty() && canAdd(stack)) {
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
            ItemStack result = remainder > 0 ? stack.copyWithCount(remainder) : ItemStack.EMPTY;
            onChanged(true);
            return result;
        }
        return stack;
    }

    public boolean accepts(ItemStack stack) {
        return this.filter.test(stack);
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (canExtract()) {
            if (!simulate) {
                ItemStack stack = this.content.split(amount);
                onChanged(true);
                return stack;
            }
            return this.content.copyWithCount(Math.min(amount, this.content.getCount()));
        }
        return ItemStack.EMPTY;
    }

    protected void onChanged(boolean notify) {
        if (notify) {
            this.parent.onChanged();
            this.onChanged.accept(getItem());
        }
    }

    private boolean canAdd(ItemStack stack) {
        return accepts(stack) && (this.content.isEmpty() || ItemStack.isSameItemSameComponents(this.content, stack));
    }

    @Override
    public void serialize(ValueOutput valueOutput) {
        if (!this.content.isEmpty()) {
            valueOutput.store("item", ItemStack.CODEC, this.content);
        }
    }

    @Override
    public void deserialize(ValueInput valueInput) {
        this.content = valueInput.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
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
