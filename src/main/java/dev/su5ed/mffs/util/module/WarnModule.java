package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.util.BiometricIdentity;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WarnModule extends BaseInterdictionModule {

    public WarnModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix matrix, LivingEntity target) {
        if (target instanceof Player player && !BiometricIdentity.isAccessGranted(matrix.getBiometricIdentifiers(), player, FieldPermission.BYPASS_DEFENSE)) {
            player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.no_entry", matrix.getTitle()), false);
        }
        return false;
    }
}
