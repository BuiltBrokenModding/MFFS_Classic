package com.mffs.model;

import cofh.api.energy.TileEnergyHandler;
import com.mffs.api.IActivatable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by pwaln on 6/1/2016.
 */
public abstract class TileMFFS extends TileEntity implements IActivatable {

    /* If this machine is on */
    private boolean isActivated;

    /* If this tile requires a restone signal */
    private boolean isProvidingSignal;

    /* Ticks */
    protected long ticks;

    @Override
    public void updateEntity() {
        if(ticks == 0) {
            start();
        } else if(ticks >= Long.MAX_VALUE) {
            ticks = 1;
        }
        ticks++;
    }

    /* Starts the entity */
    public void start(){};

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("mffs_isActive", isActivated);
        nbt.setBoolean("mffs_redstone", isProvidingSignal);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        isActivated = nbt.getBoolean("mffs_isActive");
        isProvidingSignal = nbt.getBoolean("mffs_redstone");
    }

    /**
     * Gets if this machine requires a restone signal.
     *
     * @return
     */
    public boolean requiresRedstone() {
        return isProvidingSignal;
    }

    /**
     * Sets the redstone required status.
     *
     * @param b
     */
    public void setRedstoneRequired(boolean b) {
        this.isProvidingSignal = b;
    }

    @Override
    public boolean isActive() {
        return isActivated;
    }

    @Override
    public void setActive(boolean on) {
        if (!on && (isProvidingSignal || worldObj.isRemote)) {
            return;
        }
        this.isActivated = on;
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }
}
