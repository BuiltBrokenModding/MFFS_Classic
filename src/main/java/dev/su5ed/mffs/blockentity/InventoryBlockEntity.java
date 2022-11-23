package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.util.InventorySlot;
import dev.su5ed.mffs.util.SlotItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public abstract class InventoryBlockEntity extends BaseBlockEntity {
    protected final SlotItemHandler items;
    private final LazyOptional<IItemHandler> itemCap;

    protected InventoryBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.items = new SlotItemHandler(this::onInventoryChanged);
        this.itemCap = LazyOptional.of(() -> this.items);
    }

    protected InventorySlot addSlot(String name, InventorySlot.Mode mode) {
        return addSlot(name, mode, stack -> true);
    }

    protected InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter) {
        return this.items.addSlot(name, mode, filter);
    }

    protected void onInventoryChanged() {}

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
