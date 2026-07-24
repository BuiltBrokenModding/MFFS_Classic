package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.BiometricIdentifierLink;
import dev.su5ed.mffs.api.security.FieldPermission;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class BiometricIdentity {
    public static boolean isAccessGranted(Collection<BiometricIdentifier> identifiers, LivingEntity entity, FieldPermission permission) {
        return identifiers.stream().anyMatch(id -> id.isActive() && id.isAccessGranted(entity, permission));
    }

    public static boolean isWarpAuthorized(BiometricIdentifierLink link, LivingEntity entity) {
        return entity instanceof Player player && player.isCreative()
            || BiometricIdentity.isAccessGranted(link.getBiometricIdentifiers(), entity, FieldPermission.WARP_SNEAK)
            || BiometricIdentity.isAccessGranted(link.getBiometricIdentifiers(), entity, FieldPermission.WARP_WALK);
    }
    
    public static boolean isWalkWarpAuthorized(BiometricIdentifierLink link, LivingEntity entity) {
        return entity instanceof Player player && player.isCreative()
            || BiometricIdentity.isAccessGranted(link.getBiometricIdentifiers(), entity, FieldPermission.WARP_WALK);
    }
}
