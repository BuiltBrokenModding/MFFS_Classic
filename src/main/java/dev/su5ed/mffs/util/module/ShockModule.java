package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShockModule extends BaseModule {
    public ShockModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onCollideWithForceField(World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return super.onCollideWithForceField(world, pos, entity);

        // Creative players are never shocked.
        if (entity instanceof EntityPlayer player && player.capabilities.isCreativeMode) {
            return super.onCollideWithForceField(world, pos, entity);
        }

        // Authorized players are never shocked.
        if (entity instanceof EntityPlayer player) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof ForceFieldBlockEntity forceField) {
                Projector projector = forceField.getProjector().orElse(null);
                if (projector != null) {
                    BiometricIdentifier identifier = projector.getBiometricIdentifier();
                    if (identifier != null && identifier.isAccessGranted(player, FieldPermission.WARP)) {
                        return super.onCollideWithForceField(world, pos, entity);
                    }
                }
            }
        }

        float damage = this.stack.getCount() * MFFSConfig.shockModuleDamagePerModule;
        float knockback = MFFSConfig.doShockModuleKnockback
            ? this.stack.getCount() * MFFSConfig.shockModuleKnockbackStrengthPerModule
            : 0F;
        ModUtil.shockEntity(entity, damage, pos, knockback);
        return super.onCollideWithForceField(world, pos, entity);
    }
}
