package dev.su5ed.mffs.api.fortron;

import dev.su5ed.mffs.api.FrequencyBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public interface FortronStorage extends INBTSerializable<CompoundTag>, FrequencyBlock, IEnergyStorage {
    /**
     * @return the owning block entity
     */
    BlockEntity getOwner();

    /**
     * Sets the amount of fortron energy.
     *
     * @param energy the amount of energy to store
     */
    void setStoredFortron(int energy);

    /**
     * @return The amount of fortron stored.
     */
    int getFortronStored();

    /**
     * @return The maximum possible amount of fortron that can be stored.
     */
    int getMaxFortron();

    /**
     * Called to use and consume fortron energy from this storage unit.
     *
     * @param joules   Amount of fortron energy to use.
     * @param simulate whether to execute or simulate the operation
     * @return The amount of energy that was actually provided.
     */
    int extractFortron(int joules, boolean simulate);

    /**
     * Called to use and give fortron energy from this storage unit.
     *
     * @param joules   Amount of fortron energy to give.
     * @param simulate whether to execute or simulate the operation
     * @return The amount of energy that was actually injected.
     */
    int receiveFortron(int joules, boolean simulate);
}
