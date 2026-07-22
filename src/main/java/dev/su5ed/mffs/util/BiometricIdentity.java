package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;

public class BiometricIdentity {
    public static boolean isAccessGranted(Collection<BiometricIdentifier> identifiers, LivingEntity entity, FieldPermission permission) {
        return identifiers.stream().anyMatch(id -> id.isActive() && id.isAccessGranted(entity, permission));
    }
}
