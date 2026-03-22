package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.Collection;

public class ConfiscationModule extends BaseInterdictionModule {
    public ConfiscationModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, EntityLivingBase target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            BiometricIdentifier identifier = interdictionMatrix.getBiometricIdentifier();
            if (identifier != null && identifier.isAccessGranted(player, FieldPermission.BYPASS_CONFISCATION)) {
                return false;
            }
            if (identifier == null || !identifier.isAccessGranted(player, FieldPermission.BYPASS_DEFENSE)) {
                Collection<ItemStack> filteredItems = interdictionMatrix.getFilteredItems();
                InterdictionMatrix.ConfiscationMode mode = interdictionMatrix.getConfiscationMode();
                int confiscationCount = 0;
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack checkStack = player.inventory.getStackInSlot(i);
                    if (!checkStack.isEmpty()) {
                        boolean stacksMatch = filteredItems.stream().anyMatch(s -> s.isItemEqual(checkStack));
                        if ((mode == InterdictionMatrix.ConfiscationMode.BLACKLIST && stacksMatch)
                            || (mode == InterdictionMatrix.ConfiscationMode.WHITELIST && !stacksMatch)) {
                            interdictionMatrix.mergeIntoInventory(player.inventory.getStackInSlot(i));
                            player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                            confiscationCount++;
                        }
                    }
                }
                if (confiscationCount > 0) {
                    String key = confiscationCount == 1 ? "interdiction_matrix.confiscation_singular" : "interdiction_matrix.confiscation_plural";
                    player.sendStatusMessage(ModUtil.translate("info", key, interdictionMatrix.getTitle(), confiscationCount), false);
                    TileEntity be = interdictionMatrix.be();
                    if (be.hasCapability(ModCapabilities.FORTRON, null)) {
                        FortronStorage fortron = be.getCapability(ModCapabilities.FORTRON, null);
                        if (fortron != null) {
                            fortron.extractFortron(confiscationCount, false);
                        }
                    }
                }
            }
        }
        return false;
    }
}
