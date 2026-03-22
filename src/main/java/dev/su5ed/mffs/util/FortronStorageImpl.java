package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class FortronStorageImpl implements FortronStorage {
    private final TileEntity owner;
    private int capacity;
    private int stored;
    private int frequency;
    private final Runnable onContentsChanged;

    public FortronStorageImpl(TileEntity owner, int capacity, Runnable onContentsChanged) {
        this.owner = owner;
        this.capacity = capacity;
        this.onContentsChanged = onContentsChanged;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (this.stored > capacity) {
            this.stored = capacity;
            this.onContentsChanged.run();
        }
    }

    @Override
    public TileEntity getOwner() {
        return this.owner;
    }

    @Override
    public int getStoredFortron() {
        return this.stored;
    }

    @Override
    public void setStoredFortron(int energy) {
        int clamped = Math.max(0, Math.min(energy, this.capacity));
        if (this.stored != clamped) {
            this.stored = clamped;
            this.onContentsChanged.run();
        }
    }

    @Override
    public int getFortronCapacity() {
        return this.capacity;
    }

    @Override
    public int extractFortron(int ml, boolean simulate) {
        int extracted = Math.min(ml, this.stored);
        if (!simulate && extracted > 0) {
            this.stored -= extracted;
            this.onContentsChanged.run();
        }
        return extracted;
    }

    @Override
    public int insertFortron(int ml, boolean simulate) {
        int space = this.capacity - this.stored;
        int inserted = Math.min(ml, space);
        if (!simulate && inserted > 0) {
            this.stored += inserted;
            this.onContentsChanged.run();
        }
        return inserted;
    }

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public void setFrequency(int frequency) {
        if (this.frequency != frequency) {
            this.frequency = frequency;
            this.onContentsChanged.run();
        }
    }

    /** Write fortron data to NBT. Call from owning TileEntity.writeToNBT(). */
    public void writeNbt(NBTTagCompound tag) {
        tag.setInteger("fortronStored", this.stored);
        tag.setInteger("fortronCapacity", this.capacity);
        tag.setInteger("frequency", this.frequency);
    }

    /** Read fortron data from NBT. Call from owning TileEntity.readFromNBT(). */
    public void readNbt(NBTTagCompound tag) {
        this.stored    = tag.getInteger("fortronStored");
        this.capacity  = tag.getInteger("fortronCapacity");
        this.frequency = tag.getInteger("frequency");
    }
}
