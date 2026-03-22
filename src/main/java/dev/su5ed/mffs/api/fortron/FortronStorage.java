package dev.su5ed.mffs.api.fortron;

import dev.su5ed.mffs.api.FrequencyBlock;
import net.minecraft.tileentity.TileEntity;

public interface FortronStorage extends FrequencyBlock {
    /**
     * @return the owning tile entity
     */
    TileEntity getOwner();

    /**
     * Sets the amount of fortron energy.
     *
     * @param energy the amount of energy to store
     */
    void setStoredFortron(int energy);

    /**
     * @return The amount of fortron stored.
     */
    int getStoredFortron();

    /**
     * @return The maximum possible amount of fortron that can be stored.
     */
    int getFortronCapacity();

    /**
     * Called to use and consume fortron energy from this storage unit.
     *
     * @param joules   Amount of fortron energy to use.
     * @param simulate If true, only simulate the operation without actually consuming.
     * @return The amount of energy that was actually provided.
     */
    int extractFortron(int joules, boolean simulate);

    /**
     * Called to inject fortron energy into this storage unit.
     *
     * @param joules   Amount of fortron energy to give.
     * @param simulate If true, only simulate the operation without actually injecting.
     * @return The amount of energy that was actually injected.
     */
    int insertFortron(int joules, boolean simulate);
}
