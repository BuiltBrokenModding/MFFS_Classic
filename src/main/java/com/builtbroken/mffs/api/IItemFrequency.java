package com.builtbroken.mffs.api;

import net.minecraft.item.ItemStack;

/**
 * Applied to all items that has a frequency.
 *
 * @author Calclavia
 */

public interface IItemFrequency
{
    /**
     * @param itemStack - Pass an ItemStack if dealing with items with frequencies.
     * @return The frequency of this object.
     */
    int getFrequency(ItemStack itemStack);

    /**
     * Sets the frequency
     *
     * @param frequency - The frequency of this object.
     * @param itemStack - Pass an ItemStack if dealing with items with frequencies.
     */
    void setFrequency(int frequency, ItemStack itemStack);
}
