package dev.su5ed.mffs.api.card;

import com.mojang.authlib.GameProfile;
import dev.su5ed.mffs.api.security.FieldPermission;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IdentificationCard extends INBTSerializable<CompoundTag> {
    boolean hasPermission(FieldPermission permission);
    
    Collection<FieldPermission> getPermissions();
    
    void setPermissions(Collection<FieldPermission> permissions);

    void addPermission(FieldPermission permission);

    void removePermission(FieldPermission permission);

    @Nullable
    GameProfile getIdentity();

    void setIdentity(GameProfile profile);
    
    boolean checkIdentity(Player player);
    
    void copyTo(IdentificationCard other);
}
