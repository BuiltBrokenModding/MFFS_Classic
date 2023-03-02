package dev.su5ed.mffs.util.inventory;

import com.mojang.authlib.GameProfile;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.security.FieldPermission;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CopyingIdentificationCard implements IdentificationCard {
    private final IdentificationCard card;
    private final IdentificationCard copy;

    public CopyingIdentificationCard(IdentificationCard card, IdentificationCard copy) {
        this.card = card;
        this.copy = copy;
    }

    @Override
    public boolean hasPermission(FieldPermission permission) {
        return this.card.hasPermission(permission);
    }

    @Override
    public Collection<FieldPermission> getPermissions() {
        return this.card.getPermissions();
    }

    @Override
    public void setPermissions(Collection<FieldPermission> permissions) {
        this.card.setPermissions(permissions);
        this.card.copyTo(this.copy);
    }

    @Override
    public void addPermission(FieldPermission permission) {
        this.card.addPermission(permission);
        this.card.copyTo(this.copy);
    }

    @Override
    public void removePermission(FieldPermission permission) {
        this.card.removePermission(permission);
        this.card.copyTo(this.copy);
    }

    @Nullable
    @Override
    public GameProfile getIdentity() {
        return this.card.getIdentity();
    }

    @Override
    public void setIdentity(GameProfile profile) {
        this.card.setIdentity(profile);
        this.card.copyTo(this.copy);
    }

    @Override
    public boolean checkIdentity(Player player) {
        return this.card.checkIdentity(player);
    }

    @Override
    public void copyTo(IdentificationCard other) {
        this.card.copyTo(other);
    }
}
