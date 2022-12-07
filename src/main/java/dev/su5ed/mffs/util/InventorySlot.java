package dev.su5ed.mffs.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventorySlot implements INBTSerializable<CompoundTag> {
    private final SlotItemHandler parent;
    private final String name;
    private final Mode mode;
    private final Predicate<ItemStack> filter;
    private final Consumer<ItemStack> onChanged;

    private ItemStack content = ItemStack.EMPTY;
    
    public InventorySlot(SlotItemHandler parent, String name, Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged) {
        this.parent = parent;
        this.name = name;
        this.mode = mode;
        this.filter = filter;
        this.onChanged = onChanged;
    }

    public String getName() {
        return this.name;
    }
    
    public boolean canInsert(ItemStack stack) {
        return this.mode.input && this.filter.test(stack);
    }
    
    public boolean canExtract(int amount) {
        return this.mode.output;
    }

    public ItemStack getItem() {
        return this.content;
    }
    
    public void setItem(ItemStack stack) {
        setItem(stack, true);
    }
    
    public void setItem(ItemStack stack, boolean notify) {
        this.content = stack;
        onChanged(notify);
    }
    
    public ItemStack extract(int amount) {
        ItemStack stack = this.content.split(amount);
        onChanged(true);
        return stack;
    }
    
    public boolean isEmpty() {
        return this.content.isEmpty();
    }
    
    protected void onChanged(boolean notify) {
        if (notify) {
            this.parent.onChanged();
        }
        this.onChanged.accept(getItem());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        this.content.save(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.content = ItemStack.of(nbt);
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
