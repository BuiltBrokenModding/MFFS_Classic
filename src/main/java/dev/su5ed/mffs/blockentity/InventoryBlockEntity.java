package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.util.inventory.InventorySlot;
import dev.su5ed.mffs.util.inventory.InventorySlotItemHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class InventoryBlockEntity extends BaseBlockEntity {
    protected final InventorySlotItemHandler items;

    protected InventoryBlockEntity() {
        super();
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

    protected InventorySlot addSlot(String name, InventorySlot.Mode mode, Predicate<ItemStack> filter,
                                     Consumer<ItemStack> onChanged, Function<ItemStack, Integer> capacityProvider) {
        return this.items.addSlot(name, mode, filter, onChanged, false, capacityProvider);
    }

    protected InventorySlot addVirtualSlot(String name) {
        return this.items.addSlot(name, InventorySlot.Mode.NONE, stack -> true, stack -> {}, true);
    }

    protected void onInventoryChanged() {
        markDirty();
    }

    @Override
    public void provideAdditionalDrops(List<? super ItemStack> drops) {
        super.provideAdditionalDrops(drops);
        drops.addAll(this.items.getAllItems());
    }

    /**
     * Tries to insert a stack into adjacent inventories via IItemHandler capability.
     * If insertion fails, spawns the item in the world.
     */
    public boolean mergeIntoInventory(ItemStack stack) {
        if (!this.world.isRemote && !stack.isEmpty()) {
            ItemStack remainder = stack.copy();
            for (EnumFacing side : EnumFacing.values()) {
                TileEntity neighbor = this.world.getTileEntity(this.pos.offset(side));
                IItemHandler handler = neighbor != null
                    ? neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())
                    : null;
                if (handler != null) {
                    remainder = ItemHandlerHelper.insertItemStacked(handler, remainder, false);
                    if (remainder.isEmpty()) break;
                }
            }
            if (!remainder.isEmpty()) {
                EntityItem item = new EntityItem(this.world,
                    this.pos.getX() + 0.5, this.pos.getY() + 1, this.pos.getZ() + 0.5, remainder);
                this.world.spawnEntity(item);
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Capability exposure: IItemHandler
    // -------------------------------------------------------------------------

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.items;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected void saveCommonTag(NBTTagCompound compound) {
        super.saveCommonTag(compound);
        compound.setTag("items", this.items.serializeNBT());
    }

    @Override
    protected void loadCommonTag(NBTTagCompound compound) {
        super.loadCommonTag(compound);
        if (compound.hasKey("items")) {
            this.items.deserializeNBT(compound.getCompoundTag("items"));
            // Ensure derived state is recalculated after NBT load (modules, upgrades, etc.)
            this.onInventoryChanged();
        }
    }
}
