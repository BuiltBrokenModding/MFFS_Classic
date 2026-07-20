package dev.su5ed.mffs.api.security;

import dev.su5ed.mffs.api.card.IdentificationCard;
import net.minecraft.world.entity.LivingEntity;
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
     * Is access granted to this specific entity?
     *
     * @param entity     - entity.
     * @param permission - The permission.
     */
    boolean isAccessGranted(LivingEntity entity, FieldPermission permission);

    /**
     * Is the biometric identifier currently active?
     */
    boolean isActive();

    /**
     * Gets the card currently placed in the manipulating slot.
     */
    Optional<IdentificationCard> getManipulatingCard();
}
