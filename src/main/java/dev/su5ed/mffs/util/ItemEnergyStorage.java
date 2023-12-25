package dev.su5ed.mffs.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ItemEnergyStorage implements IEnergyStorage {
    private final ItemStack stack;
    private final int capacity;
    private final int maxTransfer;

    public ItemEnergyStorage(ItemStack stack, int capacity, int maxTransfer) {
        this.stack = stack;
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }

    private void setEnergyStored(int energy) {
        this.stack.getOrCreateTag().putInt("energy", energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }
        int energyStored = getEnergyStored();
        int energyReceived = Math.min(getMaxEnergyStored() - energyStored, Math.min(this.maxTransfer, maxReceive));
        if (!simulate) {
            setEnergyStored(energyStored + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }
        int energyStored = getEnergyStored();
        int energyExtracted = Math.min(energyStored, Math.min(this.maxTransfer, maxExtract));
        if (!simulate) {
            setEnergyStored(energyStored - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return this.stack.getOrCreateTag().getInt("energy");
    }

    @Override
    public int getMaxEnergyStored() {
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
