package dev.su5ed.mffs.util;

import net.minecraftforge.energy.EnergyStorage;

import java.util.function.BooleanSupplier;

public class CustomEnergyStorage extends EnergyStorage {
    private final BooleanSupplier canReceive;
    private final Runnable onChanged;

    public CustomEnergyStorage(int capacity, int maxTransfer, BooleanSupplier canReceive, Runnable onChanged) {
        super(capacity, maxTransfer, maxTransfer);

        this.canReceive = canReceive;
        this.onChanged = onChanged;
    }

    protected void onEnergyChanged() {
        this.onChanged.run();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int rc = super.receiveEnergy(maxReceive, simulate);
        if (rc > 0 && !simulate) {
            onEnergyChanged();
        }
        return rc;
    }

    public int extractEnergy() {
        return extractEnergy(this.maxExtract, false);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int rc = super.extractEnergy(maxExtract, simulate);
        if (rc > 0 && !simulate) {
            onEnergyChanged();
        }
        return rc;
    }

    @Override
    public boolean canReceive() {
        return super.canReceive() && this.canReceive.getAsBoolean();
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        onEnergyChanged();
    }

    public void addEnergy(int energy) {
        this.energy += energy;
        if (this.energy > getMaxEnergyStored()) {
            this.energy = getEnergyStored();
        }
        onEnergyChanged();
    }

    public void consumeEnergy(int energy) {
        this.energy -= energy;
        if (this.energy < 0) {
            this.energy = 0;
        }
        onEnergyChanged();
    }

    public void setMaxTransfer(int maxTransfer) {
        this.maxReceive = maxTransfer;
        this.maxExtract = maxTransfer;
    }

    public int getRequestedEnergy() {
        return getMaxEnergyStored() - getEnergyStored();
    }

    public boolean canExtract(int extract) {
        return extractEnergy(extract, true) >= extract;
    }
}
