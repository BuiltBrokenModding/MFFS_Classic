package dev.su5ed.mffs.api.security;

import net.minecraft.world.item.ItemStack;

/**
 * Applied to Biometric Identifiers (extends TileEntity).
 */
public interface BiometricIdentifier {
    /**
     * Is access granted to this specific user?
     *
     * @param username   - Minecraft username.
     * @param permission - The permission.
     */
    boolean isAccessGranted(String username, Permission permission);

    /**
     * Gets the owner of the security center.
     */
    String getOwner();

    /**
     * Gets the card currently placed in the manipulating slot.
     */
    ItemStack getManipulatingCard();
}
