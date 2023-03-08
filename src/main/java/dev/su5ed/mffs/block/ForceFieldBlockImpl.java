package dev.su5ed.mffs.block;

import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ForceFieldBlockImpl extends AbstractGlassBlock implements ForceFieldBlock, EntityBlock {
    private static final VoxelShape COLLIDABLE_BLOCK = Shapes.create(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

    public ForceFieldBlockImpl() {
        super(Properties.of(Material.GLASS)
            .destroyTime(-1)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false));
    }

    @Override
    public Optional<Projector> getProjector(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof ForceFieldBlockEntity forceField ? forceField.getProjector() : Optional.empty();
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return Optional.ofNullable(level.getBlockEntity(pos))
            .map(be -> be instanceof ForceFieldBlockEntity f ? f.getClientBlockLight() : null)
            .orElseGet(() -> super.getLightEmission(state, level, pos));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getProjector(level, pos)
            .map(projector -> {
                if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player) {
                    BiometricIdentifier bioIndentified = projector.getBiometricIdentifier();
                    if (player.isShiftKeyDown() && !context.isAbove(COLLIDABLE_BLOCK, pos, true) && (player.isCreative() || bioIndentified != null && bioIndentified.isAccessGranted(player, FieldPermission.WARP))) {
                        return Shapes.empty();
                    }
                }
                return null;
            })
            .orElse(COLLIDABLE_BLOCK);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        getProjector(level, pos)
            .ifPresent(projector -> {
                for (ItemStack stack : projector.getModuleStacks()) {
                    if (stack.getCapability(ModCapabilities.MODULE).map(module -> module.onCollideWithForceField(level, pos, entity, stack)).orElse(false)) {
                        return;
                    }
                }
                if (!entity.level.isClientSide && entity.distanceToSqr(new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)) < Mth.square(0.7)) {
                    if (entity instanceof LivingEntity living && (!(entity instanceof Player player) || !player.isCreative())) {
                        living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 4 * 20, 3));
                        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1));
                    }
                    BiometricIdentifier identifier = projector.getBiometricIdentifier();
                    if (!(entity instanceof Player player) || !entity.isShiftKeyDown() || !player.isCreative() && (identifier == null || !identifier.isAccessGranted(player, FieldPermission.WARP))) {
                        entity.hurt(ModObjects.FIELD_SHOCK, Integer.MAX_VALUE);
                    }
                }
            });
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModObjects.FORCE_FIELD_BLOCK_ENTITY.get().create(pos, state);
    }
}
