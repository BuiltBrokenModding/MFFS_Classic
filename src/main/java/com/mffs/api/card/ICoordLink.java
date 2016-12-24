package com.mffs.api.card;

import com.builtbroken.mc.lib.transform.vector.Location;
import net.minecraft.item.ItemStack;

/**
 * A grid ModularForcefieldSystem uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 *
 * @author Calclavia
 */
//TODO depricate and move to VE interface
public interface ICoordLink {
    void setLink(ItemStack paramItemStack, Location paramVectorWorld);

    Location getLink(ItemStack paramItemStack);
}