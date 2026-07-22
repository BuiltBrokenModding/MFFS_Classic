package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.BiometricIdentity;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AntiPersonnelModule extends BaseInterdictionModule {

    public AntiPersonnelModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix matrix, LivingEntity target) {
        if (target instanceof Player player
            && !BiometricIdentity.isAccessGranted(matrix.getBiometricIdentifiers(), player, FieldPermission.BYPASS_DEFENSE)
            && !player.isCreative() && !player.isInvulnerable()
        ) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                matrix.mergeIntoInventory(stack);
                inventory.setItem(i, ItemStack.EMPTY);
            }

            ModUtil.shockEntity(player, Integer.MAX_VALUE);
            BlockEntity be = matrix.be();
            FortronStorage fortron = be.getLevel().getCapability(ModCapabilities.FORTRON, be.getBlockPos(), be.getBlockState(), be, null);
            if (fortron != null) {
                fortron.extractFortron(MFFSConfig.COMMON.interdictionMatrixKillEnergy.get(), false);
            }
            player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.fairwell", matrix.getTitle()), false);
            return true;
        }
        return false;
    }
}
