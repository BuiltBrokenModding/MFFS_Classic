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
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ForceFieldBlockImpl extends Block implements ForceFieldBlock, EntityBlock {
    private static final VoxelShape COLLIDABLE_BLOCK = Shapes.create(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
    public static final BooleanProperty PROPAGATES_SKYLIGHT = BooleanProperty.create("propagates_skylight");
    public static final BooleanProperty SOLID = BooleanProperty.create("solid");

    public ForceFieldBlockImpl(Properties properties) {
        super(properties
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((a, b, c) -> false)
            .isSuffocating((a, b, c) -> false)
            .isViewBlocking((a, b, c) -> false)
            .destroyTime(-1)
            .strength(-1.0F, 3600000.0F)
            .noLootTable());

        registerDefaultState(this.stateDefinition.any().setValue(PROPAGATES_SKYLIGHT, true).setValue(SOLID, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PROPAGATES_SKYLIGHT, SOLID);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext)
            .setValue(PROPAGATES_SKYLIGHT, true)
            .setValue(SOLID, true);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(PROPAGATES_SKYLIGHT);
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
    protected VoxelShape getOcclusionShape(BlockState state) {
        return state.getValue(SOLID) ? Shapes.block() : Shapes.empty();
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
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
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
            .or(() -> getCamouflageBlock(level, pos)
                .filter(this::preventStackOverflow)
                .map(block -> block.getCollisionShape(level, pos, context)))
            .orElse(COLLIDABLE_BLOCK);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        super.entityInside(state, level, pos, entity, applier, intersects);

        getProjector(level, pos)
            .ifPresent(projector -> {
                for (Module module : projector.getModuleInstances()) {
                    if (module.onCollideWithForceField(level, pos, entity)) {
                        return;
                    }
                }
                if (!entity.level().isClientSide() && entity.distanceToSqr(new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)) < Mth.square(0.7)) {
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
                            living.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 4 * 20, 3));
                            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 1));
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
