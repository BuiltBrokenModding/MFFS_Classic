package dev.su5ed.mffs.util;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * Implements {@link IEnergyStorage} and delegates all calls to a
 * wrapped storage instance, short-circuiting {@link #receiveEnergy} /
 * {@link #extractEnergy} when the respective flag is {@code false}.
 */
public class SidedEnergyWrapper implements IEnergyStorage {
    private final IEnergyStorage wrapped;
    private final boolean canExtract;
    private final boolean canReceive;

    public SidedEnergyWrapper(IEnergyStorage wrapped, boolean canExtract, boolean canReceive) {
        this.wrapped    = wrapped;
        this.canExtract = canExtract;
        this.canReceive = canReceive;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!this.canReceive) return 0;
        return this.wrapped.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!this.canExtract) return 0;
        return this.wrapped.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored()    { return this.wrapped.getEnergyStored(); }

    @Override
    public int getMaxEnergyStored() { return this.wrapped.getMaxEnergyStored(); }

    @Override
    public boolean canExtract() { return this.canExtract && this.wrapped.canExtract(); }

    @Override
    public boolean canReceive() { return this.canReceive && this.wrapped.canReceive(); }
}
