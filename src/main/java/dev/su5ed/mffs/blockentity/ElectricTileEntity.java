package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.util.CustomEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public abstract class ElectricTileEntity extends ModularBlockEntity {
    public final CustomEnergyStorage energy;

    protected ElectricTileEntity(int capacity) {
        super();
        this.energy = new CustomEnergyStorage(capacity, Integer.MAX_VALUE, this::isActive, this::markDirty);
    }

    /**
     * Get the IEnergyStorage for the given side (null = any side).
     * Subclasses may return null to disable energy I/O on certain sides.
     */
    public IEnergyStorage getEnergy(EnumFacing side) {
        return this.energy;
    }

    /**
     * Charges an electric item from this machine's energy storage (energy -> item).
     */
    public void chargeItemFromSelf(ItemStack stack) {
        IEnergyStorage receiver = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (receiver != null && receiver.canReceive()) {
            int extracted = this.energy.extractEnergy(Integer.MAX_VALUE, true);
            int inserted = receiver.receiveEnergy(extracted, false);
            this.energy.extractEnergy(inserted, false);
        }
    }

    /**
     * Discharges an electric item into this machine's energy storage (item -> energy).
     */
    public void dischargeItemIntoSelf(ItemStack stack) {
        IEnergyStorage source = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (source != null && source.canExtract()) {
            int requested = this.energy.getMaxEnergyStored() - this.energy.getEnergyStored();
            int extracted = source.extractEnergy(requested, true);
            int inserted = this.energy.receiveEnergy(extracted, false);
            source.extractEnergy(inserted, false);
        }
    }

    /**
     * Pushes energy from this machine to adjacent tiles that accept RF/FE.
     */
    protected void outputEnergyToNearbyTiles() {
        for (EnumFacing facing : getEnergyOutputSides()) {
            if (this.energy.getEnergyStored() <= 0) break;
            net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(this.pos.offset(facing));
            if (te != null) {
                IEnergyStorage handler = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (handler != null && handler.canReceive()) {
                    int toSend = this.energy.extractEnergy(Integer.MAX_VALUE, true);
                    int sent = handler.receiveEnergy(toSend, false);
                    this.energy.extractEnergy(sent, false);
                }
            }
        }
    }

    public Set<EnumFacing> getEnergyInputSides() {
        return Collections.emptySet();
    }

    public Set<EnumFacing> getEnergyOutputSides() {
        return Collections.emptySet();
    }

    // -------------------------------------------------------------------------
    // Capability exposure: IEnergyStorage (Forge Energy / RF)
    // -------------------------------------------------------------------------

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return getEnergy(facing) != null;
        }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) getEnergy(facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected void saveTag(NBTTagCompound compound) {
        super.saveTag(compound);
        compound.setTag("energy", this.energy.serializeNBT());
    }

    @Override
    protected void loadTag(NBTTagCompound compound) {
        super.loadTag(compound);
        if (compound.hasKey("energy")) {
            this.energy.deserializeNBT(compound.getCompoundTag("energy"));
        }
    }
}
