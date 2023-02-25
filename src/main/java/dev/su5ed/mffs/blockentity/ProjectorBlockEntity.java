package dev.su5ed.mffs.blockentity;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.ObjectCache;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.Module.ProjectAction;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.network.UpdateAnimationSpeed;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModSounds;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import dev.su5ed.mffs.util.projector.CustomProjectorMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ProjectorBlockEntity extends ModularBlockEntity implements Projector {
    private static final String TRANSLATION_CACHE_KEY = "getTranslation";
    private static final String POSITIVE_SCALE_CACHE_KEY = "getPositiveScale";
    private static final String NEGATIVE_SCALE_CACHE_KEY = "getNegativeScale";
    private static final String ROTATION_YAW_CACHE_KEY = "getRotationYaw";
    private static final String ROTATION_PITCH_CACHE_KEY = "getRotationPitch";
    private static final String ROTATION_ROLL_CACHE_KEY = "getRotationRoll";
    private static final String INTERIOR_POINTS_CACHE_KEY = "getInteriorPoints";

    private final LazyOptional<Projector> projectorOptional = LazyOptional.of(() -> this);
    private final List<ScheduledEvent> scheduledEvents = new ArrayList<>();
    public final InventorySlot secondaryCard;
    public final InventorySlot projectorModeSlot;
    public final ListMultimap<Direction, InventorySlot> fieldModuleSlots;
    public final List<InventorySlot> upgradeSlots;

    private final Set<BlockPos> forceFields = new HashSet<>();
    private List<BlockPos> calculatedField = List.of();
    private ProjectionStage stage = ProjectionStage.OFF;
    private int clientAnimationSpeed;

    public ProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), pos, state, 50);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, ModUtil::isCard);
        this.projectorModeSlot = addSlot("projectorMode", InventorySlot.Mode.BOTH, ModUtil::isProjectorMode);
        this.fieldModuleSlots = StreamEx.of(Direction.values())
            .flatMap(side -> IntStreamEx.range(2)
                .mapToEntry(i -> side, i -> addSlot("field_module_" + side.getName() + "_" + i, InventorySlot.Mode.BOTH, ModUtil::isModule)))
            .toListAndThen(ImmutableListMultimap::copyOf);
        this.upgradeSlots = createUpgradeSlots(6, true);
    }

    public int computeAnimationSpeed() {
        int speed = 4;
        int fortronCost = getFortronCost();
        if (isActive() && getMode().isPresent() && this.fortronStorage.extractFortron(fortronCost, true) >= fortronCost) {
            speed *= fortronCost / 8.0f;
        }
        return Math.min(120, speed);
    }

    public int getAnimationSpeed() {
        return this.clientAnimationSpeed;
    }

    public void setClientAnimationSpeed(int clientAnimationSpeed) {
        if (!this.level.isClientSide) {
            throw new IllegalStateException("Must only be called on the client");
        }
        this.clientAnimationSpeed = clientAnimationSpeed;
    }

    @Override
    public BlockEntity be() {
        return this;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        reCalculateForceField();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.PROJECTOR.orEmpty(cap, this.projectorOptional);
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
        list.addAll(this.fieldModuleSlots.values());
    }

    @Override
    public void tickServer() {
        super.tickServer();

        Iterator<ScheduledEvent> it = this.scheduledEvents.iterator();
        while (it.hasNext()) {
            ScheduledEvent event = it.next();

            if (event.countDown()) {
                event.runnable.run();
                it.remove();
            }
        }

        int fortronCost = getFortronCost();
        if (isActive() && getMode().isPresent() && this.fortronStorage.extractFortron(fortronCost, true) >= fortronCost) {
            consumeCost();

            if (getTicks() % 10 == 0) {
                if (this.stage != ProjectionStage.PROJECTING) {
                    reCalculateForceField();
                } else {
                    projectField();
                }
            }

            if (getTicks() % (2 * 20) == 0 && !hasModule(ModModules.SILENCE)) {
                this.level.playSound(null, this.worldPosition, ModSounds.FIELD.get(), SoundSource.BLOCKS, 0.4F, 1 - this.level.random.nextFloat() * 0.1F);
            }
        } else {
            destroyField();
        }

        int speed = computeAnimationSpeed();
        if (speed != this.clientAnimationSpeed) {
            this.clientAnimationSpeed = speed;
            sendToChunk(new UpdateAnimationSpeed(this.worldPosition, speed));
        }
    }

    @Override
    public void blockRemoved() {
        destroyField();
        super.blockRemoved();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ProjectorMenu(containerId, this.worldPosition, player, inventory);
    }

    @Override
    protected int doGetFortronCost() {
        return super.doGetFortronCost() + 5;
    }

    @Override
    public float getAmplifier() {
        return Math.max(Math.min(getCalculatedField().size() / 1000, 10), 1);
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();

        destroyField();
        // Update mode light
        if (!this.level.isClientSide) {
            this.level.getChunkSource().getLightEngine().checkBlock(this.worldPosition);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("animationSpeed", computeAnimationSpeed());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.clientAnimationSpeed = tag.getInt("animationSpeed");
    }

    @Override
    public Optional<ProjectorMode> getMode() {
        return getModeStack().getCapability(ModCapabilities.PROJECTOR_MODE).resolve();
    }

    @Override
    public ItemStack getModeStack() {
        return this.projectorModeSlot.getItem();
    }

    @Override
    public Collection<InventorySlot> getSlotsFromSide(Direction side) {
        return this.fieldModuleSlots.get(side);
    }

    @Override
    public Collection<InventorySlot> getUpgradeSlots() {
        return this.upgradeSlots;
    }

    @Override
    public <T extends Item & Module> int getSidedModuleCount(T module, Direction... sides) {
        return StreamEx.of(sides.length == 0 ? Direction.values() : sides)
            .mapToInt(side -> getModuleCount(module, getSlotsFromSide(side)))
            .sum();
    }

    @Override
    public BlockPos getTranslation() {
        return cached(TRANSLATION_CACHE_KEY, () -> {
            int zTranslationNeg = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(Direction.NORTH));
            int zTranslationPos = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(Direction.SOUTH));

            int xTranslationNeg = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(Direction.WEST));
            int xTranslationPos = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(Direction.EAST));

            int yTranslationPos = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(Direction.UP));
            int yTranslationNeg = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(Direction.DOWN));

            return new BlockPos(xTranslationPos - xTranslationNeg, yTranslationPos - yTranslationNeg, zTranslationPos - zTranslationNeg);
        });
    }

    @Override
    public BlockPos getPositiveScale() {
        return cached(POSITIVE_SCALE_CACHE_KEY, () -> {
            int zScalePos = getModuleCount(ModModules.SCALE, getSlotsFromSide(Direction.SOUTH));
            int xScalePos = getModuleCount(ModModules.SCALE, getSlotsFromSide(Direction.EAST));
            int yScalePos = getModuleCount(ModModules.SCALE, getSlotsFromSide(Direction.UP));

            int omnidirectionalScale = getModuleCount(ModModules.SCALE, getUpgradeSlots());

            zScalePos += omnidirectionalScale;
            xScalePos += omnidirectionalScale;
            yScalePos += omnidirectionalScale;

            return new BlockPos(xScalePos, yScalePos, zScalePos);
        });
    }

    @Override
    public BlockPos getNegativeScale() {
        return cached(NEGATIVE_SCALE_CACHE_KEY, () -> {
            int zScaleNeg = getModuleCount(ModModules.SCALE, getSlotsFromSide(Direction.NORTH));
            int xScaleNeg = getModuleCount(ModModules.SCALE, getSlotsFromSide(Direction.WEST));
            int yScaleNeg = getModuleCount(ModModules.SCALE, getSlotsFromSide(Direction.DOWN));

            int omnidirectionalScale = getModuleCount(ModModules.SCALE, getUpgradeSlots());

            zScaleNeg += omnidirectionalScale;
            xScaleNeg += omnidirectionalScale;
            yScaleNeg += omnidirectionalScale;

            return new BlockPos(xScaleNeg, yScaleNeg, zScaleNeg);
        });
    }

    @Override
    public int getRotationYaw() {
        return cached(ROTATION_YAW_CACHE_KEY, () -> {
            int rotation = getModuleCount(ModModules.ROTATION, getSlotsFromSide(Direction.EAST))
                - getModuleCount(ModModules.ROTATION, getSlotsFromSide(Direction.WEST));
            return rotation * 2;
        });
    }

    @Override
    public int getRotationPitch() {
        return cached(ROTATION_PITCH_CACHE_KEY, () -> {
            int rotation = getModuleCount(ModModules.ROTATION, getSlotsFromSide(Direction.UP))
                - getModuleCount(ModModules.ROTATION, getSlotsFromSide(Direction.DOWN));
            return rotation * 2;
        });
    }

    @Override
    public int getRotationRoll() {
        return cached(ROTATION_ROLL_CACHE_KEY, () -> {
            int rotation = getModuleCount(ModModules.ROTATION, getSlotsFromSide(Direction.SOUTH))
                - getModuleCount(ModModules.ROTATION, getSlotsFromSide(Direction.NORTH));
            return rotation * 2;
        });
    }

    @Override
    public Collection<BlockPos> getCalculatedField() {
        return this.calculatedField;
    }

    @Override
    public Set<BlockPos> getInteriorPoints() {
        return cached(INTERIOR_POINTS_CACHE_KEY, () -> {
            Set<Vec3> interiorPoints = getMode().orElseThrow().getInteriorPoints(this);
            BlockPos translation = this.worldPosition.offset(getTranslation());
            int rotationYaw = getRotationYaw();
            int rotationPitch = getRotationPitch();
            int rotationRoll = getRotationRoll();
            return StreamEx.of(interiorPoints)
                .map(pos -> rotationYaw != 0 || rotationPitch != 0 || rotationRoll != 0 ? ModUtil.rotateByAngleExact(pos, rotationYaw, rotationPitch, rotationRoll) : pos)
                .map(pos -> new BlockPos(pos).offset(translation))
                .toSet();
        });
    }

    @Override
    public void projectField() {
        if (!this.level.isClientSide && this.stage == ProjectionStage.PROJECTING) {
            if (this.forceFields.isEmpty() && getModeStack().getItem() instanceof ObjectCache cache) {
                cache.clearCache();
            }
            List<BlockPos> fieldToBeProjected = new ArrayList<>(this.calculatedField);
            if (getModules().stream().anyMatch(m -> m.beforeProject(this, fieldToBeProjected))) {
                return;
            }
            int constructionSpeed = Math.min(getProjectionSpeed(), MFFSConfig.COMMON.maxFFGenPerTick.get());
            fieldLoop:
            for (int i = 0, constructionCount = 0; i < fieldToBeProjected.size() && constructionCount <= constructionSpeed; i++) {
                BlockPos pos = fieldToBeProjected.get(i);
                if (canProjectPos(pos)) {
                    for (Module module : getModules()) {
                        ProjectAction action = module.onProject(this, pos);

                        if (action == ProjectAction.SKIP) {
                            continue fieldLoop;
                        } else if (action == ProjectAction.INTERRUPT) {
                            break fieldLoop;
                        }
                    }

                    BlockState state = ModBlocks.FORCE_FIELD.get().defaultBlockState();
                    this.level.setBlock(pos, state, Block.UPDATE_NONE);
                    // Set the controlling projector of the force field block to this one
                    this.level.getBlockEntity(pos, ModObjects.FORCE_FIELD_BLOCK_ENTITY.get())
                        .ifPresent(be -> {
                            be.setProjector(this.worldPosition);
                            Block camouflage = getCamoBlock(pos);
                            if (camouflage != null) {
                                be.setCamouflage(camouflage);
                            }
                        });
                    // Only update after the projector has been set to avoid recursive remove block call from ForceFieldBlockEntity#getProjector
                    this.level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

                    this.fortronStorage.extractFortron(1, false);
                    this.forceFields.add(pos);
                    constructionCount++;
                }
            }
        }
    }

    @Override
    public void schedule(int delay, Runnable runnable) {
        this.scheduledEvents.add(new ScheduledEvent(delay, runnable));
    }

    private boolean canProjectPos(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        return (state.isAir() || getModuleCount(ModModules.DISINTEGRATION) > 0 && state.getDestroySpeed(this.level, pos) != -1 || state.getMaterial().isLiquid() || state.is(ModTags.FORCEFIELD_REPLACEABLE))
            && !state.is(ModBlocks.FORCE_FIELD.get()) && !pos.equals(this.worldPosition)
            && this.level.isLoaded(pos);
    }

    @Override
    public void destroyField() {
        if (!this.level.isClientSide && this.stage == ProjectionStage.PROJECTING) {
            StreamEx.of(this.calculatedField)
                .filter(pos -> this.level.getBlockState(pos).is(ModBlocks.FORCE_FIELD.get()))
                .forEach(pos -> this.level.removeBlock(pos, false));
        }

        this.forceFields.clear();
        this.calculatedField = List.of();
        this.stage = ProjectionStage.OFF;
    }

    @Override
    public int getProjectionSpeed() {
        return 28 + 28 * getModuleCount(ModModules.SPEED, getUpgradeSlots());
    }

    private void reCalculateForceField() {
        if (!this.level.isClientSide && this.stage != ProjectionStage.CALCULATING && getMode().isPresent()) {
            if (getModeStack().getItem() instanceof ObjectCache cache) {
                cache.clearCache();
            }

            this.forceFields.clear();
            this.calculatedField = List.of();
            this.stage = ProjectionStage.CALCULATING;
            calculateField(this).thenAccept(list -> {
                this.stage = ProjectionStage.PROJECTING;
                this.calculatedField = list;
            });
        }
    }

    public Block getCamoBlock(BlockPos pos) {
        if (!this.level.isClientSide && getModuleCount(ModModules.CAMOUFLAGE) > 0) {
            if (getMode().orElse(null) instanceof CustomProjectorMode custom) {
                Map<BlockPos, Block> map = custom.getFieldBlockMap(this, getModeStack());
                if (map != null) {
                    BlockPos fieldCenter = this.worldPosition.offset(getTranslation());
                    BlockPos relativePosition = pos.subtract(fieldCenter);
                    BlockPos rotated = ModUtil.rotateByAngle(relativePosition, -getRotationYaw(), -getRotationPitch(), -getRotationRoll());
                    Block block = map.get(rotated);
                    if (block != null) {
                        return block;
                    }
                }
            }
            return getAllModuleItemsStream()
                .mapPartial(ProjectorBlockEntity::getFilterBlock)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    private CompletableFuture<List<BlockPos>> calculateField(Projector projector) {
        return CompletableFuture.supplyAsync(this::calculateFieldPositions)
            .whenComplete((list, ex) -> {
                for (Module module : projector.getModules()) {
                    module.onCalculate(projector, projector.getCalculatedField());
                }
                Collections.shuffle(list);
            })
            .exceptionally(throwable -> {
                MFFSMod.LOGGER.error("Error calculating force field", throwable);
                return List.of();
            });
    }

    private List<BlockPos> calculateFieldPositions() {
        ProjectorMode mode = getMode().orElseThrow();
        Set<Vec3> fieldPoints = hasModule(ModModules.INVERTER) ? mode.getInteriorPoints(this) : mode.getExteriorPoints(this);
        BlockPos translation = getTranslation();
        int rotationYaw = getRotationYaw();
        int rotationPitch = getRotationPitch();
        int rotationRoll = getRotationRoll();

        return StreamEx.of(fieldPoints)
            .map(pos -> rotationYaw != 0 || rotationPitch != 0 || rotationRoll != 0 ? ModUtil.rotateByAngleExact(pos, rotationYaw, rotationPitch, rotationRoll) : pos)
            .map(pos -> pos.add(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()).add(translation.getX(), translation.getY(), translation.getZ()))
            .filter(pos -> pos.y() <= this.level.getHeight())
            .map(pos -> new BlockPos(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z)))
            .toList();
    }

    public static Optional<Block> getFilterBlock(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block.defaultBlockState().getRenderShape() == RenderShape.MODEL) {
                return Optional.of(block);
            }
        }
        return Optional.empty();
    }

    private static class ScheduledEvent {
        public final Runnable runnable;
        public int ticks;

        public ScheduledEvent(int ticks, Runnable runnable) {
            this.ticks = ticks;
            this.runnable = runnable;
        }

        public boolean countDown() {
            return --this.ticks <= 0;
        }
    }
    
    private enum ProjectionStage {
        OFF,
        CALCULATING,
        PROJECTING
    }
}