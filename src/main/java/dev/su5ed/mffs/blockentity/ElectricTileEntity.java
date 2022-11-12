package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.util.CustomEnergyStorage;
import dev.su5ed.mffs.util.SidedEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class ElectricTileEntity extends ModularBlockEntity {
    protected final CustomEnergyStorage energy;
    private final Map<Direction, LazyOptional<IEnergyStorage>> sidedEnergyCap;

    protected ElectricTileEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);

        this.energy = new CustomEnergyStorage(capacity, Integer.MAX_VALUE, this::isActive, this::setChanged);
        Set<Direction> inputSides = getEnergyInputSides();
        Set<Direction> outputSides = getEnergyOutput();
        this.sidedEnergyCap = StreamEx.of(inputSides)
            .append(outputSides)
            .distinct()
            .<LazyOptional<IEnergyStorage>>mapToEntry(side -> LazyOptional.of(() -> new SidedEnergyWrapper(this.energy, side == null || inputSides.contains(side), side == null || outputSides.contains(side))))
            .toMap();
    }

    /**
     * Charges electric item.
     */
    public void charge(ItemStack stack) {
        stack.getCapability(ForgeCapabilities.ENERGY)
            .ifPresent(energy -> this.energy.extractEnergy(energy.receiveEnergy(this.energy.getEnergyStored(), false), false));
    }

    /**
     * Discharges electric item.
     */
    public void discharge(ItemStack stack) {
        stack.getCapability(ForgeCapabilities.ENERGY)
            .ifPresent(energy -> this.energy.receiveEnergy(energy.extractEnergy(this.energy.getRequestedEnergy(), false), false));
    }

    protected long produce() {
        long totalUsed = 0;
        for (Direction direction : getEnergyOutput()) {
            if (this.energy.getEnergyStored() > 0) {
                BlockEntity be = this.level.getBlockEntity(this.worldPosition.relative(direction));
                if (be != null) {
                    int received = be.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite())
                        .map(energy -> energy.receiveEnergy(this.energy.extractEnergy(this.energy.getEnergyStored(), true), false))
                        .orElse(0);
                    totalUsed += this.energy.extractEnergy(received, false);
                }
            }
        }
        return totalUsed;
    }

    public Set<Direction> getEnergyInputSides() {
        return Collections.emptySet();
    }

    public Set<Direction> getEnergyOutput() {
        return Collections.emptySet();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.sidedEnergyCap.values().forEach(LazyOptional::invalidate);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && this.sidedEnergyCap.containsKey(side)) {
            return this.sidedEnergyCap.get(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveTag(CompoundTag tag) {
        super.saveTag(tag);

        tag.put("energy", this.energy.serializeNBT());
    }

    @Override
    protected void loadTag(CompoundTag tag) {
        super.loadTag(tag);

        this.energy.deserializeNBT(tag.get("energy"));
    }
}
