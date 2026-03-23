package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.MFFSConfig;
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
import net.minecraft.util.DamageSource;

public class AntiPersonnelModule extends BaseInterdictionModule {
    public AntiPersonnelModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, EntityLivingBase target) {
        BiometricIdentifier identifier = interdictionMatrix.getBiometricIdentifier();
        // Anti-Personnel requires an active Biometric Identifier.
        // Without one, the module is dormant — no kills without access control.
        if (identifier == null || !identifier.isActive()) return false;
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            if (!identifier.isAccessGranted(player, FieldPermission.BYPASS_DEFENSE)
                && !player.isCreative() && !player.capabilities.disableDamage) {
                // Confiscate all items
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack invStack = player.inventory.getStackInSlot(i);
                    interdictionMatrix.mergeIntoInventory(invStack);
                    player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
                float damage = MFFSConfig.antiPersonnelDamagePerSecond
                    * (MFFSConfig.interdictionMatrixActionTickRate / 20.0F)
                    * this.stack.getCount();
                player.attackEntityFrom(DamageSource.GENERIC, damage);
                // Drain fortron
                TileEntity be = interdictionMatrix.be();
                if (be.hasCapability(ModCapabilities.FORTRON, null)) {
                    FortronStorage fortron = be.getCapability(ModCapabilities.FORTRON, null);
                    if (fortron != null) {
                        fortron.extractFortron(MFFSConfig.interdictionMatrixKillEnergy, false);
                    }
                }
                player.sendStatusMessage(ModUtil.translate("info", "interdiction_matrix.fairwell", interdictionMatrix.getTitle()), false);
                return true;
            }
        }
        return false;
    }
}
