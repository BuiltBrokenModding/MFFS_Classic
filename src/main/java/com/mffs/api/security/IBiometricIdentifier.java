package com.mffs.api.security;

import net.minecraft.item.ItemStack;

/**
 * @author Calclavia
 */
public interface IBiometricIdentifier {
    boolean isAccessGranted(String paramString, Permission paramPermission);

    String getOwner();

    ItemStack getManipulatingCard();
}