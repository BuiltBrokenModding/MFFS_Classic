package dev.su5ed.mffs.util.module;

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
import one.util.streamex.StreamEx;

import java.util.Collection;

public class ConfiscationModule extends BaseInterdictionModule {

    public ConfiscationModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix matrix, LivingEntity target) {
        if (!(target instanceof Player player)) {
            return false;
        }
        
        if (BiometricIdentity.isAccessGranted(matrix.getBiometricIdentifiers(), player, FieldPermission.BYPASS_CONFISCATION)) {
            return false;
        }

        if (!BiometricIdentity.isAccessGranted(matrix.getBiometricIdentifiers(), player, FieldPermission.BYPASS_DEFENSE)) {
            Inventory inventory = player.getInventory();
            Collection<ItemStack> filteredItems = matrix.getFilteredItems();
            int confiscationCount = 0;

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack checkStack = inventory.getItem(i);
                if (!checkStack.isEmpty()) {
                    boolean stacksMatch = StreamEx.of(filteredItems).anyMatch(stack -> ItemStack.isSameItem(stack, checkStack));
                    InterdictionMatrix.ConfiscationMode mode = matrix.getConfiscationMode();
                    if (mode == InterdictionMatrix.ConfiscationMode.BLACKLIST && stacksMatch || mode == InterdictionMatrix.ConfiscationMode.WHITELIST && !stacksMatch) {
                        matrix.mergeIntoInventory(inventory.getItem(i));
                        inventory.setItem(i, ItemStack.EMPTY);
                        confiscationCount++;
                    }
                }
            }

            if (confiscationCount > 0) {
                player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.confiscation_" + (confiscationCount == 1 ? "singular" : "plural"), matrix.getTitle(), confiscationCount), false);
                final int finalConfiscationCount = confiscationCount;
                matrix.be().getCapability(ModCapabilities.FORTRON)
                    .ifPresent(fortron -> fortron.extractFortron(finalConfiscationCount, false));
            }
        }

        return false;
    }
}
