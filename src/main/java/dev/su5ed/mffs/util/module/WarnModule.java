package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WarnModule extends BaseInterdictionModule {
    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target) {
        BiometricIdentifier identifier = interdictionMatrix.getBiometricIdentifier();
        if (target instanceof Player player && (identifier == null || !identifier.isAccessGranted(player, FieldPermission.BYPASS_DEFENSE))) {
            player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.no_entry", interdictionMatrix.getDisplayName()), false);
        }
        return false;
    }
}
