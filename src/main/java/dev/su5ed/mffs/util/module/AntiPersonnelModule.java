package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AntiPersonnelModule extends BaseInterdictionModule {

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target) {
        BiometricIdentifier identifier = interdictionMatrix.getBiometricIdentifier();
        if (target instanceof Player player && (identifier == null || !identifier.isAccessGranted(player, FieldPermission.BYPASS_DEFENSE)) && !player.isCreative() && !player.isInvulnerable()) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                interdictionMatrix.mergeIntoInventory(stack);
                inventory.setItem(i, ItemStack.EMPTY);
            }

            player.hurt(ModObjects.FIELD_SHOCK, Integer.MAX_VALUE);
            interdictionMatrix.be().getCapability(ModCapabilities.FORTRON).ifPresent(fortron -> fortron.extractFortron(MFFSConfig.COMMON.interdictionMatrixKillEnergy.get(), false));
            player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.fairwell", interdictionMatrix.getDisplayName()), false);
            return true;
        }
        return false;
    }
}