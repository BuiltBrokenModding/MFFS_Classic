package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ShockModule extends BaseModule {

    public ShockModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onCollideWithForceField(Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living) {
            boolean isAuthorizedPlayer = false;
            if (entity instanceof Player player && level.getBlockState(pos).getBlock() instanceof ForceFieldBlock ffBlock) {
                Optional<Projector> projector = ffBlock.getProjector(level, pos);
                if (projector.isPresent()) {
                    BiometricIdentifier identifier = projector.get().getBiometricIdentifier();
                    isAuthorizedPlayer = player.isCreative() || identifier != null && identifier.isAccessGranted(player, FieldPermission.WARP);
                }
            }

            // Apply shock module damage when instant death is disabled
            // Creative players never take damage
            // Authorized players can disable shock damage with config
            // Unauthorized players and non-players ALWAYS take shock damage (no config to disable)
            boolean applyDamage = MFFSConfig.COMMON.disableForceFieldInstantDeathForAuthorizedPlayers.get()
                && (!(entity instanceof Player player) || !player.isCreative() && (!isAuthorizedPlayer
                    || !MFFSConfig.COMMON.disableShockModuleDamageForAuthorizedPlayers.get()));

            if (applyDamage) {
                ModUtil.shockEntity(living, this.stack.getCount());
            }
        }
        return super.onCollideWithForceField(level, pos, entity);
    }
}