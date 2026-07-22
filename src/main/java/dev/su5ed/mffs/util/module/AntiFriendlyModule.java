package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.util.BiometricIdentity;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class AntiFriendlyModule extends BaseInterdictionModule {
    public AntiFriendlyModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix matrix, LivingEntity target) {
        if (shouldRemoveEntity(matrix, target)) {
            ModUtil.shockEntity(target, Integer.MAX_VALUE);
        }
        return false;
    }

    private boolean shouldRemoveEntity(InterdictionMatrix matrix, LivingEntity target) {
        if (!(target instanceof Mob) || !target.getType().getCategory().isFriendly()) {
            return false;
        }

        if (BiometricIdentity.isAccessGranted(matrix.getBiometricIdentifiers(), target, FieldPermission.BYPASS_DEFENSE)) {
            return false;
        }

        return true;
    }
}
