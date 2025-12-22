package dev.su5ed.mffs.api.fortron;

import dev.su5ed.mffs.api.FrequencyBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public interface FortronStorage extends ValueIOSerializable, FrequencyBlock {
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
    int getStoredFortron();

    /**
     * @return The maximum possible amount of fortron that can be stored.
     */
    int getFortronCapacity();

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
    int insertFortron(int joules, boolean simulate);
}
