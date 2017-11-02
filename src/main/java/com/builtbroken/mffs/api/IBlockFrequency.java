package com.builtbroken.mffs.api;

/**
 * Applied to all blocks that has a frequency.
 *
 * @author Calclavia
 */
@Deprecated
public interface IBlockFrequency
{
    /**
     * @return The frequency of this object.
     */
    int getFrequency();

    /**
     * Sets the frequency
     *
     * @param frequency - The frequency of this object.
     */
    void setFrequency(int frequency);
}
