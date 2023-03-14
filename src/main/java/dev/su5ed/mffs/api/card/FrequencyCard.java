package dev.su5ed.mffs.api.card;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface FrequencyCard extends INBTSerializable<CompoundTag> {
    int getFrequency();

    void setFrequency(int frequency);
}
