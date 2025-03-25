package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.util.inventory.InventorySlot;
import dev.su5ed.mffs.util.inventory.InventorySlotItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class InventoryBlockEntity extends BaseBlockEntity {
    protected final InventorySlotItemHandler items;
    private final LazyOptional<IItemHandler> itemCap;

    protected InventoryBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.items = new InventorySlotItemHandler(this::onInventoryChanged);
        this.itemCap = LazyOptional.of(() -> this.items);
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
                IItemHandler handler = Optional.ofNullable(this.level.getBlockEntity(this.worldPosition.relative(side)))
                    .flatMap(be -> be.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).resolve())
                    .orElse(null);
                if (handler != null) {
                    remainder = ItemHandlerHelper.insertItem(handler, stack, false);
                }
            }
            if (!remainder.isEmpty()) {
                ItemEntity item = new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5, remainder);
                this.level.addFreshEntity(item);
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveCommonTag(CompoundTag tag) {
        super.saveCommonTag(tag);

        tag.put("items", this.items.serializeNBT());
    }

    @Override
    protected void loadCommonTag(CompoundTag tag) {
        super.loadCommonTag(tag);

        this.items.deserializeNBT(tag.getCompound("items"));
    }
}
