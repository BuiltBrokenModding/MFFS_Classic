package dev.su5ed.mffs.blockentity;

import com.google.common.base.Suppliers;
import dev.su5ed.mffs.util.CustomEnergyStorage;
import dev.su5ed.mffs.util.SidedEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public abstract class ElectricTileEntity extends ModularBlockEntity {
    public final CustomEnergyStorage energy;
    private final Map<Direction, Supplier<IEnergyStorage>> sidedEnergyCap;

    protected ElectricTileEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);

        this.energy = new CustomEnergyStorage(capacity, Integer.MAX_VALUE, this::isActive, this::setChanged);
        Set<Direction> inputSides = getEnergyInputSides();
        Set<Direction> outputSides = getEnergyOutputSides();
        this.sidedEnergyCap = StreamEx.of(inputSides)
            .append(outputSides)
            .distinct()
            .<Supplier<IEnergyStorage>>mapToEntry(side -> Suppliers.memoize(() -> new SidedEnergyWrapper(this.energy, side == null || inputSides.contains(side), side == null || outputSides.contains(side))))
            .toMap();
    }
    
    @Nullable
    public IEnergyStorage getEnergy(Direction side) {
        Supplier<IEnergyStorage> supplier = this.sidedEnergyCap.get(side);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * Charges electric item.
     */
    public void chargeItemFromSelf(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            this.energy.extractEnergy(energy.receiveEnergy(this.energy.getEnergyStored(), false), false);
        }
    }

    /**
     * Discharges electric item.
     */
    public void dischargeItemIntoSelf(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null) {
            this.energy.receiveEnergy(energy.extractEnergy(this.energy.getRequestedEnergy(), false), false);
        }
    }

    protected long outputEnergyToNearbyTiles() {
        long totalUsed = 0;
        for (Direction direction : getEnergyOutputSides()) {
            if (this.energy.getEnergyStored() > 0) {
                int received = Optional.ofNullable(this.level.getCapability(Capabilities.EnergyStorage.BLOCK, this.worldPosition.relative(direction), direction.getOpposite()))
                    .map(energy -> energy.receiveEnergy(this.energy.extractEnergy(this.energy.getEnergyStored(), true), false))
                    .orElse(0);
                totalUsed += this.energy.extractEnergy(received, false);
            }
        }
        return totalUsed;
    }

    public Set<Direction> getEnergyInputSides() {
        return Collections.emptySet();
    }

    public Set<Direction> getEnergyOutputSides() {
        return Collections.emptySet();
    }

    public IEnergyStorage getGlobalEnergyStorage() {
        return this.energy;
    }

    @Override
    protected void saveTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveTag(tag, provider);

        tag.put("energy", this.energy.serializeNBT(provider));
    }

    @Override
    protected void loadTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadTag(tag, provider);

        this.energy.deserializeNBT(provider, tag.get("energy"));
    }
}
