package dev.su5ed.mffs.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Predicate;

public class InventorySlot implements INBTSerializable<CompoundTag> {
    private final SlotItemHandler parent;
    private final String name;
    private final Mode mode;
    private final Predicate<ItemStack> filter;

    private ItemStack content = ItemStack.EMPTY;
    
    public InventorySlot(SlotItemHandler parent, String name, Mode mode, Predicate<ItemStack> filter) {
        this.parent = parent;
        this.name = name;
        this.mode = mode;
        this.filter = filter;
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
        this.content = stack;
        this.parent.setChanged();
    }
    
    public ItemStack extract(int amount) {
        ItemStack stack = this.content.split(amount);
        this.parent.setChanged();
        return stack;
    }
    
    public boolean isEmpty() {
        return this.content.isEmpty();
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
