package dev.su5ed.mffs.api.card;

import dev.su5ed.mffs.api.security.FieldPermission;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IdentificationCard {
    boolean hasPermission(FieldPermission permission);
    
    Collection<FieldPermission> getPermissions();
    
    void setPermissions(Collection<FieldPermission> permissions);

    void addPermission(FieldPermission permission);

    void removePermission(FieldPermission permission);

    @Nullable
    CardIdentity getIdentity();

    void setIdentity(CardIdentity profile);
    
    boolean checkIdentity(LivingEntity entity);
    
    void copyTo(IdentificationCard other);
}
