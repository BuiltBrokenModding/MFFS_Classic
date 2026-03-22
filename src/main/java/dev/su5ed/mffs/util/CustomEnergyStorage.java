package dev.su5ed.mffs.util;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.function.BooleanSupplier;

public class CustomEnergyStorage implements IEnergyStorage {
    private int capacity;
    protected int maxInsert;
    protected int maxExtract;
    private int energy;
    private final BooleanSupplier canReceive;
    private final Runnable onChanged;

    public CustomEnergyStorage(int capacity, int maxTransfer, BooleanSupplier canReceive, Runnable onChanged) {
        this.capacity   = capacity;
        this.maxInsert  = maxTransfer;
        this.maxExtract = maxTransfer;
        this.canReceive = canReceive;
        this.onChanged  = onChanged;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!this.canReceive.getAsBoolean()) return 0;
        int accepted = Math.min(this.capacity - this.energy, Math.min(this.maxInsert, maxReceive));
        if (!simulate && accepted > 0) {
            this.energy += accepted;
            this.onChanged.run();
        }
        return accepted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate && extracted > 0) {
            this.energy -= extracted;
            this.onChanged.run();
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.canReceive.getAsBoolean() && this.maxInsert > 0;
    }

    public void setMaxTransfer(int maxTransfer) {
        this.maxInsert  = maxTransfer;
        this.maxExtract = maxTransfer;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        }
    }

    public int getRequestedEnergy() {
        return this.capacity - this.energy;
    }

    /** @return true if this storage can extract at least {@code extract} energy. */
    public boolean canExtract(int extract) {
        return this.energy >= extract;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, this.capacity));
    }

    /** Alias used by menu data-slot sync. */
    public int getAmountAsInt() {
        return this.energy;
    }

    /** Alias used by menu data-slot sync. */
    public void set(int value) {
        this.energy = Math.max(0, Math.min(value, this.capacity));
    }

    public net.minecraft.nbt.NBTTagCompound serializeNBT() {
        net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
        tag.setInteger("Energy", this.energy);
        tag.setInteger("Capacity", this.capacity);
        tag.setInteger("MaxInsert", this.maxInsert);
        tag.setInteger("MaxExtract", this.maxExtract);
        return tag;
    }

    public void deserializeNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        this.energy = nbt.getInteger("Energy");
    }
}
