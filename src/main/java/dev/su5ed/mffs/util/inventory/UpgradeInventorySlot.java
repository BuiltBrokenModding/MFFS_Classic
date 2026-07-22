package dev.su5ed.mffs.util.inventory;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.blockentity.ModularBlockEntity;
import dev.su5ed.mffs.setup.ModCapabilities;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class UpgradeInventorySlot extends InventorySlot {
    private final ModularBlockEntity blockEntity;

    public UpgradeInventorySlot(ModularBlockEntity blockEntity, InventorySlotItemHandler parent, String name, Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged, boolean virtual, int index) {
        super(parent, name, mode, filter.and(s -> isValidStack(s, blockEntity)), onChanged, virtual, index);
        this.blockEntity = blockEntity;
    }

    private static boolean isValidStack(ItemStack stack, ModularBlockEntity be) {
        ModuleType<?> type = stack.getCapability(ModCapabilities.MODULE_TYPE);
        if (type != null) {
            int limit = be.getModuleLimit(type);
            return limit == 0 || be.getModuleCount(type) < limit;
        }
        return false;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        ModuleType<?> type = stack.getCapability(ModCapabilities.MODULE_TYPE);
        if (type != null) {
            int limit = this.blockEntity.getModuleLimit(type);
            if (limit != 0) {
                int remaining = limit - this.blockEntity.getModuleCount(type);
                int thisSlot = getItem().getCount();
                return Math.min(thisSlot + remaining, stack.getMaxStackSize());
            }
        }
        return super.getMaxStackSize(stack);
    }
}
