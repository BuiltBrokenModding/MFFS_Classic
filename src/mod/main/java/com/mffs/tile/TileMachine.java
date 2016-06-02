package com.mffs.tile;

import cofh.api.energy.TileEnergyHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by pwaln on 6/1/2016.
 */
public class TileMachine extends TileEnergyHandler {

    /* If this machine is on */
    private boolean isActivated;

    /* If this tile requires a restone signal */
    private boolean isProvidingSignal;

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
     * Gets if this machine is active.
     * @return
     */
    public boolean isActiv() {
        return isActivated;
    }

    /**
     * Sets the Active status.
     * @param on
     */
    public void setActiv(boolean on) {
        if(!on && (isProvidingSignal || worldObj.isRemote)) {
            return;
        }
        this.isActivated = on;
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    /**
     * Gets if this machine requires a restone signal.
     * @return
     */
    public boolean requiresRedstone() { return isProvidingSignal;}

    /**
     * Sets the redstone required status.
     * @param b
     */
    public void setRedstoneRequired(boolean b) { this.isProvidingSignal = b;}
}
