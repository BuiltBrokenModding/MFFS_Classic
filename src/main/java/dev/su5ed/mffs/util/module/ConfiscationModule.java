package dev.su5ed.mffs.util.module;

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
import one.util.streamex.StreamEx;

import java.util.Collection;

public class ConfiscationModule extends BaseInterdictionModule {

    public ConfiscationModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target) {
        if (target instanceof Player player) {
            BiometricIdentifier identifier = interdictionMatrix.getBiometricIdentifier();
            if (identifier != null && identifier.isAccessGranted(player, FieldPermission.BYPASS_CONFISCATION)) {
                return false;
            }
        }

        BiometricIdentifier identifier = interdictionMatrix.getBiometricIdentifier();
        if (target instanceof Player player && (identifier == null || !identifier.isAccessGranted(player, FieldPermission.BYPASS_DEFENSE))) {
            Inventory inventory = player.getInventory();
            Collection<ItemStack> filteredItems = interdictionMatrix.getFilteredItems();
            int confiscationCount = 0;

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack checkStack = inventory.getItem(i);
                if (!checkStack.isEmpty()) {
                    boolean stacksMatch = StreamEx.of(filteredItems).anyMatch(stack -> stack.sameItem(checkStack));
                    InterdictionMatrix.ConfiscationMode mode = interdictionMatrix.getConfiscationMode();
                    if (mode == InterdictionMatrix.ConfiscationMode.BLACKLIST && stacksMatch || mode == InterdictionMatrix.ConfiscationMode.WHITELIST && !stacksMatch) {
                        interdictionMatrix.mergeIntoInventory(inventory.getItem(i));
                        inventory.setItem(i, ItemStack.EMPTY);
                        confiscationCount++;
                    }
                }
            }

            if (confiscationCount > 0) {
                player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.confiscation_" + (confiscationCount == 1 ? "singular" : "plural"), interdictionMatrix.getDisplayName(), confiscationCount), false);
                final int finalConfiscationCount = confiscationCount;
                interdictionMatrix.be().getCapability(ModCapabilities.FORTRON)
                    .ifPresent(fortron -> fortron.extractFortron(finalConfiscationCount, false));
            }
        }

        return false;
    }
}
