package dev.su5ed.mffs.api.fortron;

import dev.su5ed.mffs.api.FrequencyBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface FortronStorage extends INBTSerializable<CompoundTag>, FrequencyBlock {
    
    BlockEntity getOwner();
    
    /**
     * Sets the amount of fortron energy.
     *
     * @param energy
     */
	void setStoredFortron(int energy);

    /**
     * @return Gets the amount of fortron stored.
     */
    int getStoredFortron();

    /**
     * @return Gets the maximum possible amount of fortron that can be stored.
     */
    int getFortronCapacity();

    /**
     * Called to use and consume fortron energy from this storage unit.
     *
     * @param joules   - Amount of fortron energy to use.
     * @param simulate
     * @return joules - The amount of energy that was actually provided.
     */
    int extractFortron(int joules, boolean simulate);

    /**
     * Called to use and give fortron energy from this storage unit.
     *
     * @param joules - Amount of fortron energy to give.
     * @param simulate
     * @return joules - The amount of energy that was actually injected.
     */
    int insertFortron(int joules, boolean simulate);
}
