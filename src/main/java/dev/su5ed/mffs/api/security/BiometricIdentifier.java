package dev.su5ed.mffs.api.security;

import dev.su5ed.mffs.api.card.IdentificationCard;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Optional;

/**
 * Applied to Biometric Identifiers (extends TileEntity).
 */
public interface BiometricIdentifier {
    /**
     * Returns true when the Biometric Identifier is operational (powered and has a master card).
     * Anti-Personnel modules and other security checks use this to verify the BI is configured
     * before enforcing access control.
     */
    boolean isActive();

    /**
     * Is access granted to this specific user?
     *
     * @param player     - Player.
     * @param permission - The permission.
     */
    boolean isAccessGranted(EntityPlayer player, FieldPermission permission);

    /**
     * Gets the card currently placed in the manipulating slot.
     */
    Optional<IdentificationCard> getManipulatingCard();
}
