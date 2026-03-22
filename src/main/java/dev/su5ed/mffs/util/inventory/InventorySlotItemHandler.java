package dev.su5ed.mffs.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandlerModifiable;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InventorySlotItemHandler implements IItemHandlerModifiable {
    private final Runnable onChanged;
    private final List<InventorySlot> slots = new ArrayList<>();

    public InventorySlotItemHandler(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter) {
        return addSlot(name, mode, filter, stack -> {});
    }

    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter,
                                  Consumer<ItemStack> onChanged) {
        return addSlot(name, mode, filter, onChanged, false);
    }

    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter,
                                  Consumer<ItemStack> onChanged, boolean virtual) {
        return addSlot(name, mode, filter, onChanged, virtual, null);
    }

    public InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter,
                                  Consumer<ItemStack> onChanged, boolean virtual,
                                  @Nullable Function<ItemStack, Integer> capacityProvider) {
        InventorySlot slot = new InventorySlot(this, name, mode, filter, onChanged, virtual, capacityProvider);
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

    // -----------------------------------------------------------------------
    // IItemHandlerModifiable
    // -----------------------------------------------------------------------

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.slots.size()) {
            throw new IndexOutOfBoundsException("Slot " + slot + " not in valid range [0, " + this.slots.size() + ")");
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        this.slots.get(slot).setItem(stack);
    }

    @Override
    public int getSlots() {
        return this.slots.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.slots.get(slot).getItem();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        validateSlotIndex(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        return this.slots.get(slot).insert(stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);
        return this.slots.get(slot).extract(amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        return this.slots.get(slot).accepts(stack);
    }

    // -----------------------------------------------------------------------
    // NBT persistence (called from InventoryBlockEntity)
    // -----------------------------------------------------------------------

    public NBTTagCompound serializeNBT() {
        NBTTagList list = new NBTTagList();
        for (InventorySlot slot : this.slots) {
            NBTTagCompound slotTag = new NBTTagCompound();
            slotTag.setString("name", slot.getName());
            if (!slot.getItem().isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                slot.getItem().writeToNBT(itemTag);
                slotTag.setTag("item", itemTag);
            }
            list.appendTag(slotTag);
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("slots", list);
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("slots", 10); // 10 = NBTTagCompound type
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound slotTag = list.getCompoundTagAt(i);
            String name = slotTag.getString("name");
            // Find the matching slot by name
            for (InventorySlot slot : this.slots) {
                if (slot.getName().equals(name)) {
                    if (slotTag.hasKey("item")) {
                        slot.setItem(new ItemStack(slotTag.getCompoundTag("item")), false);
                    } else {
                        slot.setItem(ItemStack.EMPTY, false);
                    }
                    break;
                }
            }
        }
    }
}
