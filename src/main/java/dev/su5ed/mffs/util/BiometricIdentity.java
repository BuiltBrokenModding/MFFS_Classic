package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.BiometricIdentifierLink;
import dev.su5ed.mffs.api.security.FieldPermission;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class BiometricIdentity {
    public static boolean isAccessGranted(Collection<BiometricIdentifier> identifiers, Player player, FieldPermission permission) {
        return identifiers.stream().anyMatch(id -> id.isActive() && id.isAccessGranted(player, permission));
    }

    public static boolean isWarpAuthorized(BiometricIdentifierLink link, Player player) {
        return player.isCreative()
            || BiometricIdentity.isAccessGranted(link.getBiometricIdentifiers(), player, FieldPermission.WARP_SNEAK)
            || BiometricIdentity.isAccessGranted(link.getBiometricIdentifiers(), player, FieldPermission.WARP_WALK);
    }

    public static boolean isWalkWarpAuthorized(BiometricIdentifierLink link, Player player) {
        return player.isCreative()
            || BiometricIdentity.isAccessGranted(link.getBiometricIdentifiers(), player, FieldPermission.WARP_WALK);
    }
}
