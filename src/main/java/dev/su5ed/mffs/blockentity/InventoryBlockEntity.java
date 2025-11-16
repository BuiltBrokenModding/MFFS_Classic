package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.util.inventory.InventorySlot;
import dev.su5ed.mffs.util.inventory.InventorySlotItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class InventoryBlockEntity extends BaseBlockEntity {
    protected final InventorySlotItemHandler items;

    protected InventoryBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.items = new InventorySlotItemHandler(this::onInventoryChanged);
    }

    public InventorySlotItemHandler getItems() {
        return this.items;
    }

    protected InventorySlot addSlot(String name, InventorySlot.Mode mode) {
        return addSlot(name, mode, stack -> true);
    }

    protected InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter) {
        return this.items.addSlot(name, mode, filter);
    }

    protected InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter, Consumer<ItemStack> onChanged) {
        return this.items.addSlot(name, mode, filter, onChanged);
    }

    protected InventorySlot addVirtualSlot(String name) {
        return this.items.addSlot(name, InventorySlot.Mode.NONE, stack -> true, stack -> {}, true);
    }

    protected void onInventoryChanged() {
    }

    @Override
    public void provideAdditionalDrops(List<? super ItemStack> drops) {
        super.provideAdditionalDrops(drops);

        drops.addAll(this.items.getAllItems());
    }

    public boolean mergeIntoInventory(ItemStack stack) {
        if (!this.level.isClientSide && !stack.isEmpty()) {
            ItemStack remainder = stack;
            for (Direction side : Direction.values()) {
                IItemHandler handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, this.worldPosition.relative(side), side.getOpposite());
                if (handler != null) {
                    remainder = ItemHandlerHelper.insertItem(handler, remainder, false);
                    if(remainder.getCount() == 0) break;
                }
            }
            if (!remainder.isEmpty()) {
                ItemEntity item = new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5, remainder);
                this.level.addFreshEntity(item);
            }
        }
        return false;
    }

    @Override
    protected void saveCommonTag(ValueOutput output) {
        super.saveCommonTag(output);

        output.putChild("items", this.items);
    }

    @Override
    protected void loadCommonTag(ValueInput input) {
        super.loadCommonTag(input);

        input.child("items").ifPresent(this.items::deserialize);
    }
}
