package dev.su5ed.mffs.api.card;

import com.mojang.authlib.GameProfile;
import dev.su5ed.mffs.api.security.FieldPermission;
import net.minecraft.world.entity.player.Player;

public interface IdentificationCard {
    boolean hasPermission(FieldPermission permission);

    boolean addPermission(FieldPermission permission);

    boolean removePermission(FieldPermission permission);

    GameProfile getIdentity();

    void setIdentity(GameProfile profile);
    
    boolean checkIdentity(Player player);
}
