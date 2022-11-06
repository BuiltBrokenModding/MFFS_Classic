package dev.su5ed.mffs.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class InventoryBlockEntity extends BaseBlockEntity {
    protected final ItemStackHandler items;
    private final LazyOptional<IItemHandler> itemCap;

    protected InventoryBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.items = new ItemStackHandler(getSizeInventory()) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return InventoryBlockEntity.this.isItemValidForSlot(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                InventoryBlockEntity.this.onInventoryChanged(slot);
            }
        };
        this.itemCap = LazyOptional.of(() -> this.items);
    }

    public abstract int getSizeInventory();

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
    
    protected void onInventoryChanged(int slot) {
        
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemCap.cast();
        }
        return super.getCapability(cap, side);
    }
}
