package dev.su5ed.mffs.api.security;

import dev.su5ed.mffs.api.card.IdentificationCard;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Applied to Biometric Identifiers (extends TileEntity).
 */
public interface BiometricIdentifier {
    /**
     * Is access granted to this specific user?
     *
     * @param player     - Player.
     * @param permission - The permission.
     */
    boolean isAccessGranted(Player player, FieldPermission permission);

    /**
     * Gets the card currently placed in the manipulating slot.
     */
    Optional<IdentificationCard> getManipulatingCard();
}
