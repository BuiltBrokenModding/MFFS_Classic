package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class AntiPersonnelModule extends BaseInterdictionModule {

    public AntiPersonnelModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

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

            ModUtil.shockEntity(player, Integer.MAX_VALUE);
            BlockEntity be = interdictionMatrix.be();
            FortronStorage fortron = be.getLevel().getCapability(ModCapabilities.FORTRON, be.getBlockPos(), be.getBlockState(), be, null);
            if (fortron != null) {
                try (Transaction tx = Transaction.openRoot()) {
                    fortron.extractFortron(MFFSConfig.COMMON.interdictionMatrixKillEnergy.get(), tx);
                    tx.commit();
                }
            }
            player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.fairwell", interdictionMatrix.getTitle()), false);
            return true;
        }
        return false;
    }
}
