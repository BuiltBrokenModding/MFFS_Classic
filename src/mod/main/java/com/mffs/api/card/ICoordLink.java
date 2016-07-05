package com.mffs.api.card;

import mekanism.api.Coord4D;
import net.minecraft.item.ItemStack;

/**
 * A grid MFFS uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 *
 * @author Calclavia
 */
public interface ICoordLink {
    void setLink(ItemStack paramItemStack, Coord4D paramVectorWorld);

    Coord4D getLink(ItemStack paramItemStack);
}