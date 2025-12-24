package dev.su5ed.mffs.block;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.compat.CreateTrainCompat;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ForceFieldBlockImpl extends Block implements ForceFieldBlock, EntityBlock {
    private static final VoxelShape COLLIDABLE_BLOCK = Shapes.create(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

    public ForceFieldBlockImpl() {
        super(Properties.ofFullCopy(Blocks.GLASS)
            .destroyTime(-1)
            .strength(-1.0F, 3600000.0F)
            .noLootTable());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamouflageBlock(level, pos)
            .filter(this::preventStackOverflow)
            .map(block -> block.propagatesSkylightDown(level, pos))
            .orElse(true);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamouflageBlock(level, pos)
            .filter(this::preventStackOverflow)
            .map(block -> block.getShadeBrightness(level, pos))
            .orElse(1.0F);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction side) {
        return getCamouflageBlock(level, pos)
            .filter(this::preventStackOverflow)
            .flatMap(block -> getCamouflageBlock(level, pos.relative(side))
                .map(neighbor -> block.skipRendering(neighbor, side)))
            .orElseGet(() -> neighborState.is(this));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCamouflageBlock(level, pos)
            .filter(this::preventStackOverflow)
            .map(block -> block.getShape(level, pos))
            .orElseGet(() -> super.getShape(state, level, pos, context));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCamouflageBlock(level, pos)
            .filter(this::preventStackOverflow)
            .map(block -> block.getVisualShape(level, pos, context))
            .orElseGet(Shapes::empty);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamouflageBlock(level, pos)
            .filter(this::preventStackOverflow)
            .map(block -> block.getOcclusionShape(level, pos))
            .orElseGet(() -> super.getOcclusionShape(state, level, pos));
    }

    @Override
    public Optional<Projector> getProjector(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof ForceFieldBlockEntity forceField ? forceField.getProjector() : Optional.empty();
    }

    private Optional<BlockState> getCamouflageBlock(BlockGetter level, BlockPos pos) {
        return Optional.ofNullable(level.getBlockEntity(pos))
            .map(be -> be instanceof ForceFieldBlockEntity forceField ? forceField.getCamouflage() : null);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
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
                    BiometricIdentifier identifier = projector.getBiometricIdentifier();
                    if (isAuthorized(identifier, player)) {
                        // Walk-through mode: authorized players can walk through without sneaking
                        if (MFFSConfig.COMMON.allowWalkThroughForceFields.get()) {
                            // If player is standing on top, keep it solid (prevents falling through floors)
                            // Otherwise allow walk-through
                            return context.isAbove(COLLIDABLE_BLOCK, pos, true) ? null : Shapes.empty();
                        }
                        // Sneak mode: must sneak to pass through
                        else if (player.isShiftKeyDown() && !context.isAbove(COLLIDABLE_BLOCK, pos, true)) {
                            return Shapes.empty();
                        }
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
                for (Module module : projector.getModuleInstances()) {
                    if (module.onCollideWithForceField(level, pos, entity)) {
                        return;
                    }
                }
                if (!entity.level().isClientSide && entity.distanceToSqr(new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)) < Mth.square(0.7)) {
                    BiometricIdentifier identifier = projector.getBiometricIdentifier();
                    boolean isAuthorizedPlayer = entity instanceof Player player && isAuthorized(identifier, player);

                    if (entity instanceof LivingEntity living) {
                        // Apply nausea and slowness effects
                        // Creative players never get effects
                        // Authorized players don't get effects if config is enabled
                        boolean applyEffects = !(entity instanceof Player player)
                            || !player.isCreative()
                            && (!isAuthorizedPlayer || !MFFSConfig.COMMON.disableForceFieldEffectsForAuthorizedPlayers.get());

                        if (applyEffects) {
                            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 4 * 20, 3));
                            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1));
                        }
                    }

                    // Apply instant death damage
                    // Creative players never take damage
                    // If instant death is disabled for authorized players, no one takes instant death
                    // Otherwise everyone except creative takes instant death
                    boolean applyDamage = !MFFSConfig.COMMON.disableForceFieldInstantDeathForAuthorizedPlayers.get()
                        && (!(entity instanceof Player player) || !player.isCreative());

                    if (applyDamage) {
                        ModUtil.shockEntity(entity, Integer.MAX_VALUE);
                    }
                }
            });
    }

    private boolean isSneaking(Entity entity) {
        return entity.isShiftKeyDown() || CreateTrainCompat.isTrainPassenger(entity);
    }

    private boolean preventStackOverflow(BlockState state) {
        return !state.is(this);
    }

    private boolean isAuthorized(BiometricIdentifier identifier, Player player) {
        return player.isCreative() || identifier != null && identifier.isAccessGranted(player, FieldPermission.WARP);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModObjects.FORCE_FIELD_BLOCK_ENTITY.get().create(pos, state);
    }
}
