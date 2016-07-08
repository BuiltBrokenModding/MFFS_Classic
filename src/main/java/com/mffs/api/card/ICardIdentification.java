package com.mffs.api.card;

import com.mffs.api.security.Permission;
import net.minecraft.item.ItemStack;

/**
 * A grid MFFS uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 *
 * @author Calclavia
 */
public interface ICardIdentification
        extends ICard {
    boolean hasPermission(ItemStack paramItemStack, Permission paramPermission);

    boolean addPermission(ItemStack paramItemStack, Permission paramPermission);

    boolean removePermission(ItemStack paramItemStack, Permission paramPermission);

    String getUsername(ItemStack paramItemStack);

    void setUsername(ItemStack paramItemStack, String paramString);
}