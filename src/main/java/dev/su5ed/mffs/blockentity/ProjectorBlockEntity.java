package dev.su5ed.mffs.blockentity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.datafixers.util.Pair;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.item.CustomProjectorModeItem;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.network.UpdateAnimationSpeed;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModSounds;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.ObjectCache;
import dev.su5ed.mffs.util.SetBlockEvent;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProjectorBlockEntity extends ModularBlockEntity implements Projector {
    private static final String TRANSLATION_CACHE_KEY = "getTranslation";
    private static final String POSITIVE_SCALE_CACHE_KEY = "getPositiveScale";
    private static final String NEGATIVE_SCALE_CACHE_KEY = "getNegativeScale";
    private static final String ROTATION_YAW_CACHE_KEY = "getRotationYaw";
    private static final String ROTATION_PITCH_CACHE_KEY = "getRotationPitch";
    private static final String ROTATION_ROLL_CACHE_KEY = "getRotationRoll";
    private static final String INTERIOR_POINTS_CACHE_KEY = "getInteriorPoints";

    private final List<ScheduledEvent> scheduledEvents = new ArrayList<>();
    public final InventorySlot secondaryCard;
    public final InventorySlot projectorModeSlot;
    public final ListMultimap<Direction, InventorySlot> fieldModuleSlots;
    public final List<InventorySlot> upgradeSlots;

    private final Semaphore semaphore = new Semaphore();
    private final Set<BlockPos> projectedBlocks = Collections.synchronizedSet(new HashSet<>());
    private final LoadingCache<BlockPos, Pair<BlockState, Boolean>> projectionCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<>() {
            @Override
            public Pair<BlockState, Boolean> load(BlockPos key) {
                return canProjectPos(key);
            }
        });
    private int clientAnimationSpeed;

    public ProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), pos, state, 50);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, ModUtil::isCard);
        this.projectorModeSlot = addSlot("projectorMode", InventorySlot.Mode.BOTH, ModUtil::isProjectorMode, stack -> destroyField());
        this.fieldModuleSlots = StreamEx.of(Direction.values())
            .flatMap(side -> IntStreamEx.range(2)
                .mapToEntry(i -> side, i -> addSlot("field_module_" + side.getName() + "_" + i, InventorySlot.Mode.BOTH, stack -> ModUtil.isModule(stack, Module.Category.FIELD), stack -> destroyField())))
            .toListAndThen(ImmutableListMultimap::copyOf);
        this.upgradeSlots = createUpgradeSlots(6, this::isMatrixModuleOrPass, stack -> destroyField());
    }

    @SubscribeEvent
    public void onSetBlock(SetBlockEvent event) {
        if (event.getLevel() == this.level && !this.semaphore.isInStage(ProjectionStage.STANDBY) && !event.getState().is(ModBlocks.FORCE_FIELD.get())) {
            this.projectionCache.invalidate(event.getPos());
        }
    }

    private boolean isMatrixModuleOrPass(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(ModCapabilities.MODULE_TYPE))
            .map(ModuleType::getCategories)
            .map(categories -> categories.isEmpty() || categories.contains(Module.Category.MATRIX))
            .orElse(true);
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
    public BlockState getCachedBlockState(BlockPos pos) {
        return this.projectionCache.getUnchecked(pos).getFirst();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!this.level.isClientSide) {
            NeoForge.EVENT_BUS.register(this);
            reCalculateForceField();
        }
    }

    @Override
    public void setRemoved() {
        if (!this.level.isClientSide) {
            NeoForge.EVENT_BUS.unregister(this);
        }
        super.setRemoved();
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
                if (this.semaphore.isInStage(ProjectionStage.STANDBY)) {
                    reCalculateForceField();
                } else if (this.semaphore.isReady() && this.semaphore.isComplete(ProjectionStage.SELECTING)) {
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
    public void beforeBlockRemove() {
        destroyField();
        super.beforeBlockRemove();
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
        return Math.max(Math.min(getCalculatedFieldPositions().size() / 1000, 10), 1);
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();
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
        return Optional.ofNullable(getModeStack().getCapability(ModCapabilities.PROJECTOR_MODE));
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
    public Collection<TargetPosPair> getCalculatedFieldPositions() {
        return this.semaphore.getOrDefault(ProjectionStage.CALCULATING, List.of());
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
                .map(pos -> BlockPos.containing(pos).offset(translation))
                .toSet();
        });
    }

    public void projectField() {
        CompletableFuture<Void> task = this.semaphore.beginStage(ProjectionStage.PROJECTING);
        for (Module module : getModuleInstances()) {
            module.beforeProject(this);
        }
        BlockState state = ModBlocks.FORCE_FIELD.get().defaultBlockState();
        List<TargetPosPair> projectable = this.semaphore.getResult(ProjectionStage.SELECTING);
        fieldLoop:
        for (TargetPosPair pair : projectable) {
            BlockPos pos = pair.pos();
            for (Module module : getModuleInstances()) {
                Module.ProjectAction action = module.onProject(this, pos);

                if (action == Module.ProjectAction.SKIP) {
                    continue fieldLoop;
                } else if (action == Module.ProjectAction.INTERRUPT) {
                    break fieldLoop;
                }
            }

            this.level.setBlock(pos, state, Block.UPDATE_NONE);
            // Set the controlling projector of the force field block to this one
            this.level.getBlockEntity(pos, ModObjects.FORCE_FIELD_BLOCK_ENTITY.get())
                .ifPresent(be -> {
                    be.setProjector(this.worldPosition);
                    BlockState camouflage = getCamoBlock(pair.original());
                    if (camouflage != null) {
                        be.setCamouflage(camouflage);
                    }
                });
            // Only update after the projector has been set to avoid recursive remove block call from ForceFieldBlockEntity#getProjector
            this.level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

            this.fortronStorage.extractFortron(1, false);
            this.projectedBlocks.add(pos);
            this.projectionCache.invalidate(pos);
        }
        task.complete(null);
        runSelectionTask();
    }

    @Override
    public void schedule(int delay, Runnable runnable) {
        this.scheduledEvents.add(new ScheduledEvent(delay, runnable));
    }

    private Pair<BlockState, Boolean> canProjectPos(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        boolean canProject = (state.isAir() || state.liquid() || state.is(ModTags.FORCEFIELD_REPLACEABLE) || hasModule(ModModules.DISINTEGRATION) && state.getDestroySpeed(this.level, pos) != -1)
            && !state.is(ModBlocks.FORCE_FIELD.get()) && !pos.equals(this.worldPosition);
        return Pair.of(state, canProject);
    }

    @Override
    public void destroyField() {
        Collection<TargetPosPair> fieldPositions = getCalculatedFieldPositions();
        this.projectedBlocks.clear();
        this.projectionCache.invalidateAll();
        this.semaphore.reset();
        if (!this.level.isClientSide) {
            StreamEx.of(fieldPositions)
                .map(TargetPosPair::pos)
                .filter(pos -> this.level.getBlockState(pos).is(ModBlocks.FORCE_FIELD.get()))
                .forEach(pos -> this.level.removeBlock(pos, false));
        }
    }

    @Override
    public int getProjectionSpeed() {
        return 28 + 28 * getModuleCount(ModModules.SPEED, getUpgradeSlots());
    }

    @Override
    public int getModuleCount(ModuleType<?> module, Collection<InventorySlot> slots) {
        // Disable scaling of custom mode fields
        return module == ModModules.SCALE && getModeStack().getItem() instanceof CustomProjectorModeItem ? 0 : super.getModuleCount(module, slots);
    }

    private void reCalculateForceField() {
        if (getMode().isPresent()) {
            if (getModeStack().getItem() instanceof ObjectCache cache) {
                cache.clearCache();
            }
            runCalculationTask()
                .thenCompose(v -> runSelectionTask())
                .exceptionally(throwable -> {
                    MFFSMod.LOGGER.error("Error calculating force field blocks", throwable);
                    return null;
                });
        }
    }

    public BlockState getCamoBlock(Vec3 pos) {
        if (!this.level.isClientSide && hasModule(ModModules.CAMOUFLAGE)) {
            if (getModeStack().getItem() instanceof CustomProjectorModeItem custom) {
                Map<Vec3, BlockState> map = custom.getFieldBlocks(this, getModeStack());
                BlockState block = map.get(pos);
                if (block != null) {
                    return block;
                }
            }
            return getAllModuleItemsStream()
                .mapPartial(ProjectorBlockEntity::getFilterBlock)
                .findFirst()
                .map(Block::defaultBlockState)
                .orElse(null);
        }
        return null;
    }

    private CompletableFuture<?> runCalculationTask() {
        return this.semaphore.<List<TargetPosPair>>beginStage(ProjectionStage.CALCULATING)
            .completeAsync(this::calculateFieldPositions)
            .whenComplete((list, ex) -> {
                for (Module module : getModuleInstances()) {
                    module.onCalculate(this, list);
                }
                Collections.shuffle(list);
            })
            .exceptionally(throwable -> {
                MFFSMod.LOGGER.error("Error calculating force field", throwable);
                return List.of();
            });
    }

    private List<TargetPosPair> calculateFieldPositions() {
        ProjectorMode mode = getMode().orElseThrow();
        Set<Vec3> fieldPoints = hasModule(ModModules.INVERTER) ? mode.getInteriorPoints(this) : mode.getExteriorPoints(this);
        BlockPos translation = getTranslation();
        int rotationYaw = getRotationYaw();
        int rotationPitch = getRotationPitch();
        int rotationRoll = getRotationRoll();

        return StreamEx.of(fieldPoints)
            .mapToEntry(pos -> rotationYaw != 0 || rotationPitch != 0 || rotationRoll != 0 ? ModUtil.rotateByAngleExact(pos, rotationYaw, rotationPitch, rotationRoll) : pos)
            .mapValues(pos -> pos.add(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()).add(translation.getX(), translation.getY(), translation.getZ()))
            .filterValues(pos -> pos.y() <= this.level.getHeight())
            .mapKeyValue((original, pos) -> new TargetPosPair(new BlockPos((int) Math.round(pos.x), (int) Math.round(pos.y), (int) Math.round(pos.z)), original))
            .toMutableList();
    }

    private CompletableFuture<?> runSelectionTask() {
        return this.semaphore.beginStage(ProjectionStage.SELECTING)
            .completeAsync(this::selectProjectablePositions)
            .exceptionally(throwable -> {
                MFFSMod.LOGGER.error("Error selecting force field blocks", throwable);
                return List.of();
            });
    }

    private List<TargetPosPair> selectProjectablePositions() {
        if (this.projectedBlocks.isEmpty() && getModeStack().getItem() instanceof ObjectCache cache) {
            cache.clearCache();
        }
        List<TargetPosPair> fieldToBeProjected = new ArrayList<>(getCalculatedFieldPositions());
        Set<Module> modules = getModuleInstances();
        for (Module module : modules) {
            module.beforeSelect(this, fieldToBeProjected);
        }
        int constructionSpeed = Math.min(getProjectionSpeed(), MFFSConfig.COMMON.maxFFGenPerTick.get());
        List<TargetPosPair> projectable = new ArrayList<>();
        fieldLoop:
        for (int i = 0, constructionCount = 0; i < fieldToBeProjected.size() && constructionCount < constructionSpeed && !isRemoved() && this.semaphore.isInStage(ProjectionStage.SELECTING); i++) {
            TargetPosPair pair = fieldToBeProjected.get(i);
            BlockPos pos = pair.pos();
            for (Module module : modules) {
                Module.ProjectAction action = module.onSelect(this, pos);
                if (action == Module.ProjectAction.SKIP) {
                    continue fieldLoop;
                } else if (action == Module.ProjectAction.INTERRUPT) {
                    break fieldLoop;
                }
            }
            if (this.projectionCache.getUnchecked(pos).getSecond() && this.level.isLoaded(pos)) {
                projectable.add(pair);
                constructionCount++;
            }
        }
        return projectable;
    }

    public static Optional<Block> getFilterBlock(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block.defaultBlockState().getRenderShape() != RenderShape.INVISIBLE) {
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
        STANDBY,
        CALCULATING,
        SELECTING,
        PROJECTING
    }

    private static class Semaphore {
        private ProjectionStage stage = ProjectionStage.STANDBY;
        private final Map<ProjectionStage, CompletableFuture<Object>> tasks = new HashMap<>();

        @SuppressWarnings("unchecked")
        public synchronized <T> CompletableFuture<T> beginStage(ProjectionStage stage) {
            if (isReady()) {
                this.stage = stage;
                CompletableFuture<T> task = new CompletableFuture<>();
                this.tasks.put(stage, (CompletableFuture<Object>) task);
                return task;
            } else {
                throw new RuntimeException("Attempted to switch stage before it was completed");
            }
        }

        public synchronized boolean isComplete(ProjectionStage stage) {
            return this.tasks.containsKey(stage) && this.tasks.get(stage).isDone();
        }

        public synchronized boolean isInStage(ProjectionStage stage) {
            return this.stage == stage;
        }

        public synchronized boolean isReady() {
            return this.stage == ProjectionStage.STANDBY || isComplete(this.stage);
        }

        @SuppressWarnings("unchecked")
        public synchronized <T> T getResult(ProjectionStage stage) {
            CompletableFuture<T> task = (CompletableFuture<T>) this.tasks.get(stage);
            if (!task.isDone()) {
                throw new RuntimeException("Stage " + stage + " hasn't completed yet!");
            }
            return task.join();
        }

        @SuppressWarnings("unchecked")
        public synchronized <T> T getOrDefault(ProjectionStage stage, T defaultValue) {
            return this.tasks.containsKey(stage) ? (T) this.tasks.get(stage).getNow(defaultValue) : defaultValue;
        }

        public synchronized void reset() {
            this.stage = ProjectionStage.STANDBY;
            this.tasks.clear();
        }
    }
}
