package dev.su5ed.mffs.blockentity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.item.CustomProjectorModeItem;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.UpdateAnimationSpeed;
import dev.su5ed.mffs.network.UpdateBlockEntityPacket;
import dev.su5ed.mffs.setup.*;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.ObjectCache;
import dev.su5ed.mffs.util.SetBlockEvent;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ProjectorBlockEntity extends ModularBlockEntity implements Projector {
    private static final String TRANSLATION_CACHE_KEY = "getTranslation";
    private static final String POSITIVE_SCALE_CACHE_KEY = "getPositiveScale";
    private static final String NEGATIVE_SCALE_CACHE_KEY = "getNegativeScale";
    private static final String ROTATION_YAW_CACHE_KEY = "getRotationYaw";
    private static final String ROTATION_PITCH_CACHE_KEY = "getRotationPitch";
    private static final String ROTATION_ROLL_CACHE_KEY = "getRotationRoll";
    private static final String INTERIOR_POINTS_CACHE_KEY = "getInteriorPoints";

    /**
     * Upgrade-slot modules that do NOT affect field geometry and therefore should not
     * trigger a force-field regeneration when inserted or removed.
     *
     * Note: Glow Module is intentionally absent from this set.  Glow changes are
     * handled separately: they call {@link #refreshFieldLights()} to push updated
     * {@code clientBlockLight} values in-place without a field rebuild.
     *
     * @see #isGlowOrEmpty(ItemStack)
     */
    private static final Set<ModuleType<?>> REGEN_EXEMPT_MODULES;
    static {
        Set<ModuleType<?>> s = new HashSet<>();
        s.add(ModModules.SPEED);
        s.add(ModModules.CAPACITY);
        s.add(ModModules.SHOCK);
        s.add(ModModules.SPONGE);
        s.add(ModModules.COLLECTION);
        s.add(ModModules.SILENCE);
        REGEN_EXEMPT_MODULES = Collections.unmodifiableSet(s);
    }

    private final List<ScheduledEvent> scheduledEvents = new ArrayList<>();
    public final InventorySlot secondaryCard;
    public final InventorySlot projectorModeSlot;
    public final ListMultimap<EnumFacing, InventorySlot> fieldModuleSlots;
    public final List<InventorySlot> upgradeSlots;

    private final Semaphore semaphore = new Semaphore();
    private final Set<BlockPos> projectedBlocks = Collections.synchronizedSet(new HashSet<>());

    // Orphan positions left over from a soft-destroy (resize/module change).
    private final Set<BlockPos> pendingRemoval = Collections.synchronizedSet(new LinkedHashSet<>());

    // Positions whose server-side camouflage was updated in applyFieldDiff() but whose client
    // notification packet hasn't been sent yet.  Drained N-per-cycle in tickServer()
    private final Set<BlockPos> pendingCamoRefresh = Collections.synchronizedSet(new LinkedHashSet<>());

    // Positions projected in the previous session, loaded from NBT. Used once per load to diff
    // against the freshly-calculated field to find orphans.
    private final Set<BlockPos> savedProjectedBlocks = new HashSet<>();

    // Shadow set of currently-calculated field positions for O(1) lookup in the gap-fill sweep.
    // Rebuilt asynchronously in runCalculationTask
    private volatile Set<BlockPos> calculatedFieldSet = Collections.emptySet();

    // Holds a snapshot of the old projectedBlocks taken at the moment softDestroyField() was called.
    // Once complete, applyFieldDiff() diffs the snapshot against the new geometry
    // and only touches blocks that actually changed.
    private Set<BlockPos> pendingDiffSnapshot = null;

    private final LoadingCache<BlockPos, AbstractMap.SimpleEntry<IBlockState, Boolean>> projectionCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<>() {
            @Override
            public AbstractMap.SimpleEntry<IBlockState, Boolean> load(BlockPos key) {
                return canProjectPos(key);
            }
        });
    private int clientAnimationSpeed;

    // Incrementally maintained send radius. -1 means "dirty, must recompute".
    private double cachedFieldSendRadius = -1;

    // Fast camo checks — updated on inventory change, avoids synchronized map lookups per block.
    private boolean camoModulePresent;

    // Cached own-inventory camo result. null = not yet computed this inventory-change cycle.
    // Uses Optional: empty() means "scanned, nothing found", present() means a valid block.
    @Nullable
    private Optional<IBlockState> cachedOwnCamo;

    // Cached neighbor camo data for the current projection pass. Null = not yet computed this pass.
    @Nullable
    private List<IBlockState> cachedNeighborWeightedList;

    public ProjectorBlockEntity() {
        super(50);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, ModUtil::isCard);
        this.projectorModeSlot = addSlot("projectorMode", InventorySlot.Mode.BOTH, ModUtil::isProjectorMode, this::onModeChanged);
        this.fieldModuleSlots = StreamEx.of(EnumFacing.values())
            .flatMap(side -> IntStreamEx.range(2)
                .mapToEntry(i -> side, i -> addSlot("field_module_" + side.getName() + "_" + i, InventorySlot.Mode.BOTH, stack -> ModUtil.isModule(stack, Module.Category.FIELD), stack -> onFieldModuleChanged())))
            .toListAndThen(ImmutableListMultimap::copyOf);
        // Each upgrade slot gets its own closure so we can compare old vs new content.
        // Four-way decision per slot change:
        //   1. Both old and new are regen-exempt (Speed/Capacity/etc.)  → no action
        //   2. Both old and new are glow-or-empty                        → refreshFieldLights()
        //   3. Both old and new are camo-or-empty                        → refreshFieldVisuals()
        //   4. Either side is a geometry-affecting module                → softDestroyField()
        //
        // upgradeSlotListRef is populated after the stream completes so the capacity-provider
        // lambdas can safely reference all upgrade slots at insert time (not at construction time).
        AtomicReference<List<InventorySlot>> upgradeSlotListRef = new AtomicReference<>();
        this.upgradeSlots = IntStreamEx.range(6)
            .mapToObj(i -> {
                InventorySlot[] ref = new InventorySlot[1];
                ref[0] = addSlot("upgrade_" + i, InventorySlot.Mode.BOTH, this::isMatrixModuleOrPass,
                    current -> {
                        ItemStack prev = ref[0].getPreviousItem();
                        if (isExemptFromRegen(prev) && isExemptFromRegen(current)) {
                            // purely exempt modules (speed/capacity/etc.) — nothing changes
                        } else if (isGlowOrEmpty(prev) && isGlowOrEmpty(current)) {
                            // glow-only change: update light in-place, no field rebuild needed
                            refreshFieldLights();
                        } else if (isCamoModuleOrEmpty(prev) && isCamoModuleOrEmpty(current)) {
                            // camo-only change: update camouflage in-place, no field rebuild needed
                            refreshFieldVisuals();
                        } else {
                            // If a worker module (Stabilization, Disintegration) is being inserted
                            // into an active field, move all existing FF blocks into pendingRemoval
                            // so the module can access them on the next projection cycle.
                            ModuleType<?> newType = current.isEmpty() ? null
                                : current.getCapability(ModCapabilities.MODULE_TYPE, null);
                            if ((newType == ModModules.STABILIZAZION || newType == ModModules.DISINTEGRATION)
                                && !this.projectedBlocks.isEmpty()) {
                                forceReclaimField();
                            }
                            softDestroyField();
                        }
                    },
                    stack -> {
                        // Limit speed and shock modules across all upgrade slots to their configured maximums.
                        ModuleType<?> type = stack.getCapability(ModCapabilities.MODULE_TYPE, null);
                        final int maxAllowed;
                        if (type == ModModules.SPEED) maxAllowed = MFFSConfig.maxSpeedModulesProjector;
                        else if (type == ModModules.SHOCK) maxAllowed = MFFSConfig.shockModuleMaxSlotCount;
                        else return stack.getMaxStackSize();
                        List<InventorySlot> allUpgradeSlots = upgradeSlotListRef.get();
                        if (allUpgradeSlots == null) return stack.getMaxStackSize();
                        int totalInOthers = allUpgradeSlots.stream()
                            .filter(slot -> slot != ref[0])
                            .mapToInt(slot -> {
                                ItemStack content = slot.getItem();
                                if (content.isEmpty()) return 0;
                                ModuleType<?> slotType = content.getCapability(ModCapabilities.MODULE_TYPE, null);
                                return slotType == type ? content.getCount() : 0;
                            })
                            .sum();
                        return Math.max(0, Math.min(maxAllowed - totalInOthers, stack.getMaxStackSize()));
                    });
                return ref[0];
            })
            .toList();
        upgradeSlotListRef.set(this.upgradeSlots);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ModCapabilities.PROJECTOR) return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ModCapabilities.PROJECTOR) return (T) this;
        return super.getCapability(capability, facing);
    }

    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
    public void onSetBlock(SetBlockEvent event) {
        // Invalidate the projection cache for the changed position so the next gap-fill sweep
        // sees the current world state rather than a stale cached value.
        if (event.getWorld() == this.world && event.getState().getBlock() != ModBlocks.FORCE_FIELD) {
            this.projectionCache.invalidate(event.getPos());
        }
    }

    private boolean isMatrixModuleOrPass(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(ModCapabilities.MODULE_TYPE, null))
            .map(ModuleType::getCategories)
            .map(categories -> categories.isEmpty() || categories.contains(Module.Category.MATRIX))
            .orElse(true);
    }

    /**
     * Returns {@code true} if {@code stack} is empty or holds a module that is exempt from
     * triggering a force-field regeneration (i.e. it does not affect field geometry or makeup).
     * See {@link #REGEN_EXEMPT_MODULES} for the full list.
     */
    private static boolean isExemptFromRegen(ItemStack stack) {
        if (stack.isEmpty()) return true;
        ModuleType<?> type = stack.getCapability(ModCapabilities.MODULE_TYPE, null);
        return type != null && REGEN_EXEMPT_MODULES.contains(type);
    }

    /**
     * Returns {@code true} if {@code stack} is empty or is a Glow Module.
     * Used to detect upgrade-slot changes that only affect light level, allowing
     * an in-place {@link #refreshFieldLights()} instead of a full field rebuild.
     */
    private static boolean isGlowOrEmpty(ItemStack stack) {
        if (stack.isEmpty()) return true;
        ModuleType<?> type = stack.getCapability(ModCapabilities.MODULE_TYPE, null);
        return type == ModModules.GLOW;
    }

    /**
     * Returns {@code true} if {@code stack} is empty or is a Camouflage Module.
     * Used to detect upgrade-slot changes that only affect block appearance, allowing
     * an in-place {@link #refreshFieldVisuals()} instead of a full field rebuild.
     */
    private static boolean isCamoModuleOrEmpty(ItemStack stack) {
        if (stack.isEmpty()) return true;
        ModuleType<?> type = stack.getCapability(ModCapabilities.MODULE_TYPE, null);
        return type == ModModules.CAMOUFLAGE;
    }

    public int computeAnimationSpeed() {
        int speed = 1;
        int fortronCost = getFortronCost();
        // Speed up the rotor when:
        //   1. The projector is active with a mode present.
        //   2. There is something to process (field blocks, pending removals, or scheduled events).
        //   3. The Fortron reserve can cover at least one tick of operation.
        //      Uses canConsumeFieldCost to verify the projector can still afford operation.
        if (isActive() && getMode().isPresent()
                && (!this.projectedBlocks.isEmpty() || !this.pendingRemoval.isEmpty() || !this.scheduledEvents.isEmpty())
                && canConsumeFieldCost(fortronCost / 20)) {
            speed *= fortronCost / 160.0F;
        }
        return Math.min(300, speed);
    }

    public int getAnimationSpeed() {
        return this.clientAnimationSpeed;
    }

    public void setClientAnimationSpeed(int clientAnimationSpeed) {
        if (!this.world.isRemote) {
            throw new IllegalStateException("Must only be called on the client");
        }
        this.clientAnimationSpeed = clientAnimationSpeed;
    }

    @Override
    public BaseBlockEntity be() {
        return this;
    }

    public IBlockState getCachedBlockState(BlockPos pos) {
        return this.projectionCache.getUnchecked(pos).getKey();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.camoModulePresent = hasModule(ModModules.CAMOUFLAGE);
        if (!this.world.isRemote) {
            MinecraftForge.EVENT_BUS.register(this);
            reCalculateForceField();
        }
    }

    @Override
    public void invalidate() {
        if (!this.world.isRemote) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        super.invalidate();
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
        list.addAll(this.fieldModuleSlots.values());
    }

    @Override
    public int getBaseFortronTankCapacity() {
        return MFFSConfig.projectorInitialTankCapacity;
    }

    @Override
    protected int getCapacityBoostPerModule() {
        return MFFSConfig.projectorTankCapacityPerModule;
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
        boolean canOperate = isActive() && getMode().isPresent() && canConsumeFieldCost(fortronCost / 20);

        // Per-tick billing: deduct maintenance cost every tick when operating.
        if (canOperate) {
            consumeCost();
        }

        if (canOperate) {

            if (getTicks() % MFFSConfig.projectionCycleTicks == 0) {
                // One-shot orphan detection: runs once per load after the async calculation first
                // completes. Diffs previously-projected positions against the new field to enqueue
                if (!this.savedProjectedBlocks.isEmpty() && this.semaphore.isComplete(ProjectionStage.CALCULATING)) {
                    Set<BlockPos> newField = StreamEx.of(getCalculatedFieldPositions())
                        .map(TargetPosPair::pos)
                        .toSet();
                    // Collect orphans into a list and shuffle
                    List<BlockPos> orphansToQueue = new ArrayList<>();
                    for (BlockPos old : this.savedProjectedBlocks) {
                        if (!newField.contains(old)) {
                            orphansToQueue.add(old);
                        } else if (this.world.getBlockState(old).getBlock() == ModBlocks.FORCE_FIELD) {
                            // Block is still valid in the new field. Restore it to projectedBlocks
                            this.projectedBlocks.add(old);
                        }
                    }
                    Collections.shuffle(orphansToQueue);
                    this.pendingRemoval.addAll(orphansToQueue);
                    this.savedProjectedBlocks.clear();
                }

                // Diff-based transition: the async CALCULATING stage has completed while a
                // soft-destroy snapshot is pending.  Apply the diff (on the server thread),
                // then chain into SELECTING so only genuinely-new positions are evaluated.
                if (this.pendingDiffSnapshot != null && this.semaphore.isComplete(ProjectionStage.CALCULATING)) {
                    applyFieldDiff();
                    runSelectionTask().exceptionally(throwable -> {
                        MFFSMod.LOGGER.error("Error selecting force field blocks after diff", throwable);
                        return null;
                    });
                } else if (this.semaphore.isInStage(ProjectionStage.STANDBY)) {
                    reCalculateForceField();
                } else if (this.semaphore.isReady() && this.semaphore.isComplete(ProjectionStage.SELECTING)
                        && this.fortronStorage.getStoredFortron() >= fortronCost * MFFSConfig.FORTRON_TRANSFER_TICKS / 20) {
                    // Only project when the tank still holds a comfortable Fortron reserve
                    projectField();
                }

                // Gap-fill sweep: walk every position in the calculated field geometry
                if (MFFSConfig.enableFastFill && !this.calculatedFieldSet.isEmpty()) {
                    fillGaps();
                }

            }

            if (getTicks() % (2 * 20) == 0 && !hasModule(ModModules.SILENCE)
                    && this.fortronStorage.getStoredFortron() >= fortronCost * MFFSConfig.FORTRON_TRANSFER_TICKS / 20) {
                // Only play the field sound when in a fully sustained state
                this.world.playSound(null, this.pos, ModSounds.FIELD, SoundCategory.BLOCKS, 0.4F, 1 - this.world.rand.nextFloat() * 0.1F);
            }
        } else {
            // Soft power-off: on the first tick we notice power is gone, queue all projected
            // blocks for gradual removal via the drain loop below.  Subsequent ticks are no-ops
            // until all blocks drain.
            if (!this.projectedBlocks.isEmpty()) {
                this.pendingDiffSnapshot = null;
                this.semaphore.reset();
                this.projectionCache.invalidateAll();
                this.calculatedFieldSet = Collections.emptySet();
                List<BlockPos> powerOffRemoval = new ArrayList<>(this.projectedBlocks);
                Collections.shuffle(powerOffRemoval);
                this.pendingRemoval.addAll(powerOffRemoval);
                this.projectedBlocks.clear();
                this.pendingCamoRefresh.clear();
                this.invalidateFieldSendRadius();
            }
        }

        // Drain queued block removals regardless of power state — handles both module-change
        // soft-destroys (while powered) and gradual power-off removals (while unpowered).
        if (getTicks() % MFFSConfig.projectionCycleTicks == 0 && !this.pendingRemoval.isEmpty()) {
            int speed = MFFSConfig.baseProjectionSpeed + MFFSConfig.speedModuleFactor * (getModuleCount(ModModules.SPEED, getUpgradeSlots()) / MFFSConfig.drainSpeedFactor);
            int drained = 0;
            Iterator<BlockPos> orphanIt = this.pendingRemoval.iterator();
            while (orphanIt.hasNext() && drained < speed) {
                BlockPos orphan = orphanIt.next();
                orphanIt.remove();
                if (this.world.getBlockState(orphan).getBlock() == ModBlocks.FORCE_FIELD) {
                    net.minecraft.tileentity.TileEntity ote = this.world.getTileEntity(orphan);
                    if (ote instanceof ForceFieldBlockEntity offe && this.pos.equals(offe.getProjectorPos())) {
                        this.world.setBlockToAir(orphan);
                    }
                }
                drained++;
            }
        }

        // Drain queued camo visual updates at the same rate as block removals.
        // Gradual delivery avoids a single-tick packet burst when the field is large.
        if (getTicks() % MFFSConfig.projectionCycleTicks == 0 && !this.pendingCamoRefresh.isEmpty()) {
            int camoSpeed = getProjectionSpeed();
            int sent = 0;
            double camoRadius = computeFieldSendRadius();
            Iterator<BlockPos> camoIt = this.pendingCamoRefresh.iterator();
            while (camoIt.hasNext() && sent < camoSpeed) {
                BlockPos camoPos = camoIt.next();
                camoIt.remove();
                net.minecraft.tileentity.TileEntity camoTe = this.world.getTileEntity(camoPos);
                if (camoTe instanceof ForceFieldBlockEntity be) {
                    Network.sendToAllAround(new UpdateBlockEntityPacket(camoPos, be.getCustomUpdateTag()),
                        this.world, this.pos, camoRadius);
                }
                sent++;
            }
        }

        int speed = computeAnimationSpeed();
        if (speed != this.clientAnimationSpeed) {
            this.clientAnimationSpeed = speed;
            sendToChunk(new UpdateAnimationSpeed(this.pos, speed));
        }
    }

    private boolean canConsumeFieldCost(int fortronCost) {
        return this.fortronStorage.extractFortron(fortronCost, true) >= fortronCost;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos) {
        destroyField();

        super.preRemoveSideEffects(pos);
    }

    @Override
    protected int doGetFortronCost() {
        return super.doGetFortronCost() + 100; // 100 F/s base projector upkeep
    }

    /**
     * Returns 1 when the projector is active but has no active Biometric Identifier linked.
     * Used to drive the GUI warning indicator via a synced data slot.
     */
    public int getBiometricWarningFlag() {
        if (!isActive()) return 0;
        dev.su5ed.mffs.api.security.BiometricIdentifier identifier = getBiometricIdentifier();
        return (identifier == null || !identifier.isActive()) ? 1 : 0;
    }

    @Override
    public float getAmplifier() {
        return Math.max(Math.min(getCalculatedFieldPositions().size() / 1000, 10), 1);
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();
        // Refresh camo caches — hasModule() is already cleared by super.onInventoryChanged()
        this.camoModulePresent = hasModule(ModModules.CAMOUFLAGE);
        this.cachedOwnCamo = null;
        this.cachedNeighborWeightedList = null;
        // Re-check the projector block's own light level
        if (this.world != null && !this.world.isRemote) {
            this.world.checkLight(this.pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setInteger("animationSpeed", computeAnimationSpeed());
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        if (tag.hasKey("animationSpeed")) {
            this.clientAnimationSpeed = tag.getInteger("animationSpeed");
        }
    }

    @Override
    public Optional<ProjectorMode> getMode() {
        return Optional.ofNullable(getModeStack().getCapability(ModCapabilities.PROJECTOR_MODE, null));
    }

    @Override
    public ItemStack getModeStack() {
        return this.projectorModeSlot.getItem();
    }

    @Override
    public Collection<InventorySlot> getSlotsFromSide(EnumFacing side) {
        return this.fieldModuleSlots.get(side);
    }

    @Override
    public Collection<InventorySlot> getUpgradeSlots() {
        return this.upgradeSlots;
    }

    @Override
    public BlockPos getTranslation() {
        return cached(TRANSLATION_CACHE_KEY, () -> {
            int zTranslationNeg = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(EnumFacing.NORTH));
            int zTranslationPos = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(EnumFacing.SOUTH));

            int xTranslationNeg = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(EnumFacing.WEST));
            int xTranslationPos = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(EnumFacing.EAST));

            int yTranslationPos = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(EnumFacing.UP));
            int yTranslationNeg = getModuleCount(ModModules.TRANSLATION, getSlotsFromSide(EnumFacing.DOWN));

            return new BlockPos(xTranslationPos - xTranslationNeg, yTranslationPos - yTranslationNeg, zTranslationPos - zTranslationNeg);
        });
    }

    @Override
    public BlockPos getPositiveScale() {
        return cached(POSITIVE_SCALE_CACHE_KEY, () -> {
            int zScalePos = getModuleCount(ModModules.SCALE, getSlotsFromSide(EnumFacing.SOUTH));
            int xScalePos = getModuleCount(ModModules.SCALE, getSlotsFromSide(EnumFacing.EAST));
            int yScalePos = getModuleCount(ModModules.SCALE, getSlotsFromSide(EnumFacing.UP));

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
            int zScaleNeg = getModuleCount(ModModules.SCALE, getSlotsFromSide(EnumFacing.NORTH));
            int xScaleNeg = getModuleCount(ModModules.SCALE, getSlotsFromSide(EnumFacing.WEST));
            int yScaleNeg = getModuleCount(ModModules.SCALE, getSlotsFromSide(EnumFacing.DOWN));

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
            int rotation = getModuleCount(ModModules.ROTATION, getSlotsFromSide(EnumFacing.EAST))
                - getModuleCount(ModModules.ROTATION, getSlotsFromSide(EnumFacing.WEST));
            return rotation * 2;
        });
    }

    @Override
    public int getRotationPitch() {
        return cached(ROTATION_PITCH_CACHE_KEY, () -> {
            int rotation = getModuleCount(ModModules.ROTATION, getSlotsFromSide(EnumFacing.UP))
                - getModuleCount(ModModules.ROTATION, getSlotsFromSide(EnumFacing.DOWN));
            return rotation * 2;
        });
    }

    @Override
    public int getRotationRoll() {
        return cached(ROTATION_ROLL_CACHE_KEY, () -> {
            int rotation = getModuleCount(ModModules.ROTATION, getSlotsFromSide(EnumFacing.SOUTH))
                - getModuleCount(ModModules.ROTATION, getSlotsFromSide(EnumFacing.NORTH));
            return rotation * 2;
        });
    }

    @Override
    public Collection<TargetPosPair> getCalculatedFieldPositions() {
        return this.semaphore.getOrDefault(ProjectionStage.CALCULATING, Collections.emptyList());
    }

    @Override
    public Set<BlockPos> getInteriorPoints() {
        return cached(INTERIOR_POINTS_CACHE_KEY, () -> {
            Set<Vec3d> interiorPoints = getMode().orElseThrow(NoSuchElementException::new).getInteriorPoints(this);
            BlockPos translation = this.pos.add(getTranslation());
            int rotationYaw = getRotationYaw();
            int rotationPitch = getRotationPitch();
            int rotationRoll = getRotationRoll();
            return StreamEx.of(interiorPoints)
                .map(pos -> rotationYaw != 0 || rotationPitch != 0 || rotationRoll != 0 ? ModUtil.rotateByAngleExact(pos, rotationYaw, rotationPitch, rotationRoll) : pos)
                .map(pos -> new BlockPos(pos).add(translation.getX(), translation.getY(), translation.getZ()))
                .toSet();
        });
    }

    public void projectField() {
        CompletableFuture<Void> task = this.semaphore.beginStage(ProjectionStage.PROJECTING);
        for (Module module : getModuleInstances()) {
            module.beforeProject(this);
        }
        // Invalidate per-pass neighbor camo cache so each projection sweep sees fresh inventories.
        invalidateNeighborCamoCache();
        // Hoist send radius computation out of the loop — the +64 buffer covers any newly-added blocks.
        double sendRadius = computeFieldSendRadius();
        IBlockState state = ModBlocks.FORCE_FIELD.getDefaultState();
        List<TargetPosPair> projectable = this.semaphore.getResult(ProjectionStage.SELECTING);
        fieldLoop:
        for (TargetPosPair pair : projectable) {
            BlockPos pos = pair.pos();
            boolean modulePlaced = false;
            for (Module module : getModuleInstances()) {
                Module.ProjectAction action = module.onProject(this, pos);

                if (action == Module.ProjectAction.SKIP) {
                    continue fieldLoop;
                } else if (action == Module.ProjectAction.INTERRUPT) {
                    break fieldLoop;
                } else if (action == Module.ProjectAction.PLACED) {
                    modulePlaced = true;
                    break;
                }
            }

            if (modulePlaced) {
                // A module (e.g. Stabilization) placed a physical block at this position.
                // Track it so the selection pipeline knows this position is handled.
                this.pendingRemoval.remove(pos);
                this.projectedBlocks.add(pos);
                expandFieldSendRadius(pos);
                this.projectionCache.invalidate(pos);
                continue fieldLoop;
            }

            // Check if this position is already occupied by our own FF block (soft-destroy transition).
            // If so, reclaim it without re-placing or spending Fortron.
            net.minecraft.tileentity.TileEntity existingTe = this.world.getTileEntity(pos);
            boolean isOwnField = existingTe instanceof ForceFieldBlockEntity ffe && this.pos.equals(ffe.getProjectorPos());

            if (!isOwnField) {
                // Stop placing when Fortron would drop below the full-cycle reserve threshold.
                if (this.fortronStorage.getStoredFortron() <= getFortronCost() * Math.max(MFFSConfig.projectionCycleTicks, 11) / 20) break fieldLoop;
                this.world.setBlockState(pos, state, 0);
                // Set the controlling projector of the force field block to this one
                net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(pos);
                if (te instanceof ForceFieldBlockEntity be) {
                    be.setProjector(this.pos);
                    IBlockState camouflage = getCamoBlock(pair.original());
                    be.setCamouflage(camouflage);
                }
                // Only update after the projector has been set
                this.world.notifyBlockUpdate(pos, state, state, 3);
                this.fortronStorage.extractFortron(1, false);
            } else {
                // Reclaim existing block: update camouflage and push fresh clientBlockLight to clients
                if (existingTe instanceof ForceFieldBlockEntity be) {
                    IBlockState camouflage = getCamoBlock(pair.original());
                    be.setCamouflage(camouflage);
                    // Send from projector position so players near the projector receive updates
                    // for all FF blocks regardless of each block's distance from the player.
                    Network.sendToAllAround(new UpdateBlockEntityPacket(pos, be.getCustomUpdateTag()),
                        this.world, this.pos, sendRadius);
                }
            }
            // Mark as part of the new field; remove from pending-removal so it isn't deleted.
            this.pendingRemoval.remove(pos);
            this.projectedBlocks.add(pos);
            expandFieldSendRadius(pos);
            this.projectionCache.invalidate(pos);
        }
        task.complete(null);
        runSelectionTask();
    }

    @Override
    public void schedule(int delay, Runnable runnable) {
        this.scheduledEvents.add(new ScheduledEvent(delay, runnable));
    }

    private AbstractMap.SimpleEntry<IBlockState, Boolean> canProjectPos(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        // Allow projecting over our own FF blocks that remain from a previous soft-destroy —
        // they will be "reclaimed" in projectField() without being re-placed or costing Fortron.
        boolean isOwnField = state.getBlock() == ModBlocks.FORCE_FIELD && this.pendingRemoval.contains(pos);
        boolean canProject = ((state.getBlock().isAir(state, this.world, pos)
            || state.getMaterial().isLiquid()
            || ModTags.getForceFieldReplaceable().contains(state.getBlock())
            || (hasModule(ModModules.DISINTEGRATION) && state.getBlockHardness(this.world, pos) != -1))
            && state.getBlock() != ModBlocks.FORCE_FIELD
            || isOwnField)
            && !pos.equals(this.pos);
        return new AbstractMap.SimpleEntry<>(state, canProject);
    }

    /**
     * Called when a face-slot field module changes (translation, rotation, scale, etc.).
     * Face slots only accept {@link dev.su5ed.mffs.api.module.Module.Category#FIELD} modules,
     * all of which affect field geometry.  The diff-based rebuild handles camouflage and
     * light updates for unchanged blocks, so no immediate visual refresh is needed.
     */
    private void onFieldModuleChanged() {
        softDestroyField();
    }

    /**
     * Queues a camouflage+light refresh for every currently-projected block into
     * {@link #pendingCamoRefresh} for rate-limited delivery, instead of blasting all
     * blocks in a single tick.  Server-side camouflage is updated immediately so the
     * TE state is correct before packets start draining; the list is shuffled so the
     * visual update radiates in a scattered pattern rather than following hash order.
     */
    private void refreshFieldVisuals() {
        if (this.world == null || this.world.isRemote) return;
        // Invalidate per-pass camo caches so each block gets a fresh getCamoBlock() result.
        this.cachedOwnCamo = null;
        this.cachedNeighborWeightedList = null;
        List<BlockPos> toRefresh = new ArrayList<>();
        for (BlockPos pos : new HashSet<>(this.projectedBlocks)) {
            net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(pos);
            if (te instanceof ForceFieldBlockEntity be) {
                // Update server-side state immediately so the TE is consistent before packets drain.
                IBlockState newCamo = getCamoBlock(new net.minecraft.util.math.Vec3d(pos.getX(), pos.getY(), pos.getZ()));
                be.setCamouflage(newCamo);
                toRefresh.add(pos);
            }
        }
        Collections.shuffle(toRefresh);
        this.pendingCamoRefresh.addAll(toRefresh);
    }

    /**
     * Pushes a fresh {@link UpdateBlockEntityPacket} containing the current
     * {@code clientBlockLight} value to every currently-projected block without
     * touching camouflage or rebuilding the field.  Called when only the Glow
     * Module count has changed (Case 2: in-place light update).
     *
     * If the projector is off ({@code projectedBlocks} is empty) this is a no-op;
     * correct light values will be sent during the next {@code projectField()} pass.
     */
    private void refreshFieldLights() {
        if (this.world == null || this.world.isRemote) return;
        double radius = computeFieldSendRadius();
        for (BlockPos pos : new HashSet<>(this.projectedBlocks)) {
            net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(pos);
            if (te instanceof ForceFieldBlockEntity be) {
                Network.sendToAllAround(new UpdateBlockEntityPacket(pos, be.getCustomUpdateTag()),
                    this.world, this.pos, radius);
            }
        }
    }

    /**
     * Returns the radius to use for {@link Network#sendToAllAround} when pushing
     * per-block TE updates ({@code clientBlockLight}, camouflage).  Uses the
     * projector's own position as the center so a single radius value covers ALL
     * projected blocks, regardless of how far they are from the player.
     *
     * The result is cached incrementally: {@link #expandFieldSendRadius(BlockPos)}
     * extends it as blocks are added, and {@link #invalidateFieldSendRadius()} marks
     * it dirty when blocks are bulk-removed or cleared.
     */
    private double computeFieldSendRadius() {
        if (this.cachedFieldSendRadius >= 0) {
            return this.cachedFieldSendRadius;
        }
        double maxDistSq = this.projectedBlocks.stream()
            .mapToDouble(this.pos::distanceSq)
            .max()
            .orElse(0);
        this.cachedFieldSendRadius = Math.sqrt(maxDistSq) + 64;
        return this.cachedFieldSendRadius;
    }

    /** Extend the cached send radius to cover {@code pos} if it is farther away. */
    private void expandFieldSendRadius(BlockPos pos) {
        double dist = Math.sqrt(this.pos.distanceSq(pos)) + 64;
        if (dist > this.cachedFieldSendRadius) {
            this.cachedFieldSendRadius = dist;
        }
    }

    /** Mark the cached send radius as dirty so the next call recomputes from scratch. */
    private void invalidateFieldSendRadius() {
        this.cachedFieldSendRadius = -1;
    }

    private void onModeChanged(ItemStack stack) {
        // 1.21.x: this.level.getLightEngine().checkBlock(this.worldPosition)
        if (this.world != null) {
            this.world.checkLight(this.pos);
        }
        softDestroyField();
    }

    /**
     * Moves all currently-projected blocks into {@link #pendingRemoval} so that worker modules
     * (Stabilization, Disintegration) can access them during the next projection cycle.
     */
    private void forceReclaimField() {
        if (this.world == null || this.world.isRemote) return;
        // Collect all tracked FF blocks
        List<BlockPos> toReclaim = new ArrayList<>(this.projectedBlocks);
        this.projectedBlocks.clear();
        // Include any NBT-loaded blocks that haven't been diff-processed yet.
        if (!this.savedProjectedBlocks.isEmpty()) {
            toReclaim.addAll(this.savedProjectedBlocks);
            this.savedProjectedBlocks.clear();
        }
        Collections.shuffle(toReclaim);
        this.pendingRemoval.addAll(toReclaim);
        // Invalidate cache so canProjectPos re-evaluates each position
        this.projectionCache.invalidateAll();
    }

    /**
     * Soft destroy: starts a diff-based field transition.  The current projected field stays
     * visible while a new field geometry is calculated asynchronously.  When the calculation
     * completes, {@link #applyFieldDiff()} compares old vs new positions and only touches
     * blocks that actually changed
     */
    private void softDestroyField() {
        if (this.world != null && !this.world.isRemote) {
            // Snapshot the current field for diffing after the async recalculation. Include any
            // savedProjectedBlocks that haven't been diffed yet.
            Set<BlockPos> snapshot = new HashSet<>(this.projectedBlocks);
            if (!this.savedProjectedBlocks.isEmpty()) {
                snapshot.addAll(this.savedProjectedBlocks);
                this.savedProjectedBlocks.clear();
            }
            this.pendingDiffSnapshot = snapshot;
        }
        this.projectionCache.invalidateAll();
        this.semaphore.reset();
        // Start async calculation of the new field geometry.  Do NOT clear projectedBlocks —
        // the field stays visible while we wait.  applyFieldDiff() will reconcile later.
        if (getMode().isPresent()) {
            if (getModeStack().getItem() instanceof ObjectCache cache) {
                cache.clearCache();
            }
            runCalculationTask().exceptionally(throwable -> {
                MFFSMod.LOGGER.error("Error calculating force field during soft destroy", throwable);
                return null;
            });
        }
    }

    /**
     * Applies the diff between the old projected field (snapshot) and the newly-calculated
     * field geometry.  Called on the server thread once the async CALCULATING stage completes
     * and {@link #pendingDiffSnapshot} is non-null.
     */
    private void applyFieldDiff() {
        Set<BlockPos> oldField = this.pendingDiffSnapshot;
        this.pendingDiffSnapshot = null;

        // Build lookup from the new calculated field: pos → original Vec3d (for camo lookups).
        Collection<TargetPosPair> newFieldPairs = getCalculatedFieldPositions();
        Map<BlockPos, Vec3d> newFieldMap = new HashMap<>(newFieldPairs.size() * 2, 0.5f);
        for (TargetPosPair pair : newFieldPairs) {
            newFieldMap.put(pair.pos(), pair.original());
        }

        // Partition the old field into toRemove and unchanged.
        Set<BlockPos> toRemove = new HashSet<>();
        Set<BlockPos> unchanged = new HashSet<>();
        for (BlockPos pos : oldField) {
            if (newFieldMap.containsKey(pos)) {
                unchanged.add(pos);
            } else {
                toRemove.add(pos);
            }
        }

        // Shuffle toRemove before queuing so the drain order is scattered, matching the
        // random placement order used during the build phase (runCalculationTask shuffles).
        List<BlockPos> toRemoveList = new ArrayList<>(toRemove);
        Collections.shuffle(toRemoveList);

        // Zero lights only on blocks being removed.
        if (!toRemoveList.isEmpty() && getModuleCount(ModModules.GLOW) > 0) {
            zeroFieldBlockLights(toRemove);
        }

        // Queue removals and update projectedBlocks.
        this.pendingRemoval.addAll(toRemoveList);
        this.projectedBlocks.removeAll(toRemove);
        this.invalidateFieldSendRadius();

        // Invalidate cache for removed positions so gap-fill doesn't see stale state.
        for (BlockPos pos : toRemove) {
            this.projectionCache.invalidate(pos);
        }

        // Refresh camouflage on unchanged blocks.
        // Server state is updated immediately; client packets are queued for rate-limited delivery.
        if (!unchanged.isEmpty()) {
            List<BlockPos> camoToRefresh = new ArrayList<>();
            for (BlockPos pos : unchanged) {
                net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(pos);
                if (te instanceof ForceFieldBlockEntity be) {
                    Vec3d original = newFieldMap.get(pos);
                    IBlockState newCamo = getCamoBlock(original);
                    IBlockState oldCamo = be.getCamouflage();
                    if (!Objects.equals(oldCamo, newCamo)) {
                        be.setCamouflage(newCamo);
                        camoToRefresh.add(pos);
                    }
                }
            }
            Collections.shuffle(camoToRefresh);
            this.pendingCamoRefresh.addAll(camoToRefresh);
        }
    }

    /**
     * Sends {@code clientBlockLight=0} to every force field block in {@code positions}.
     * Called before a soft-destroy to prevent orphan glow on blocks sitting in the
     * pending-removal queue.  Reclaimed blocks receive a corrected value during the
     * next {@code projectField()} pass.
     * This is probably not the best way to handle this, will look into it later.
     */
    private void zeroFieldBlockLights(Set<BlockPos> positions) {
        if (this.world == null || this.world.isRemote || positions.isEmpty()) return;
        double radius = computeFieldSendRadius();
        for (BlockPos pos : new HashSet<>(positions)) {
            net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(pos);
            if (te instanceof ForceFieldBlockEntity be) {
                NBTTagCompound zeroTag = be.getCustomUpdateTag();
                zeroTag.setInteger("clientBlockLight", 0);
                Network.sendToAllAround(new UpdateBlockEntityPacket(pos, zeroTag), this.world, this.pos, radius);
            }
        }
    }

    @Override
    public void destroyField() {
        // Hard destroy: immediately remove everything (block break).
        // Also flush any blocks pending from a prior soft destroy and cancel any in-flight diff.
        this.pendingDiffSnapshot = null;
        Set<BlockPos> alsoRemove = new HashSet<>(this.pendingRemoval);
        // Include actually-placed blocks so breaking a projector mid-transition doesn't orphan them.
        alsoRemove.addAll(this.projectedBlocks);
        // Include NBT-restored blocks that haven't been reconciled with the new field yet.
        alsoRemove.addAll(this.savedProjectedBlocks);
        this.pendingRemoval.clear();
        this.pendingCamoRefresh.clear();
        this.savedProjectedBlocks.clear();
        Collection<TargetPosPair> fieldPositions = getCalculatedFieldPositions();
        this.calculatedFieldSet = Collections.emptySet();
        this.projectedBlocks.clear();
        this.projectionCache.invalidateAll();
        this.invalidateFieldSendRadius();
        this.semaphore.reset();
        if (!this.world.isRemote) {
            StreamEx.of(fieldPositions)
                .map(TargetPosPair::pos)
                .filter(pos -> this.world.getBlockState(pos).getBlock() == ModBlocks.FORCE_FIELD)
                .forEach(pos -> this.world.setBlockToAir(pos));
            alsoRemove.stream()
                .filter(pos -> this.world.getBlockState(pos).getBlock() == ModBlocks.FORCE_FIELD)
                .forEach(pos -> this.world.setBlockToAir(pos));
        }
    }

    @Override
    protected void saveTag(NBTTagCompound compound) {
        super.saveTag(compound);
        NBTTagList removalList = new NBTTagList();
        for (BlockPos pos : this.pendingRemoval) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("x", pos.getX());
            entry.setInteger("y", pos.getY());
            entry.setInteger("z", pos.getZ());
            removalList.appendTag(entry);
        }
        compound.setTag("pendingRemoval", removalList);
        NBTTagList projectedList = new NBTTagList();
        for (BlockPos pos : this.projectedBlocks) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("x", pos.getX());
            entry.setInteger("y", pos.getY());
            entry.setInteger("z", pos.getZ());
            projectedList.appendTag(entry);
        }
        compound.setTag("projectedBlocks", projectedList);
    }

    @Override
    protected void loadTag(NBTTagCompound compound) {
        super.loadTag(compound);
        this.pendingRemoval.clear();
        if (compound.hasKey("pendingRemoval")) {
            NBTTagList removalList = compound.getTagList("pendingRemoval", 10);
            for (int i = 0; i < removalList.tagCount(); i++) {
                NBTTagCompound entry = removalList.getCompoundTagAt(i);
                this.pendingRemoval.add(new BlockPos(entry.getInteger("x"), entry.getInteger("y"), entry.getInteger("z")));
            }
        }
        // Restore previously-projected positions for the one-shot orphan diff in tickServer.
        // These are NOT put into pendingRemoval here — tickServer diffs them against the
        // freshly-calculated field and only enqueues genuine orphans.
        this.savedProjectedBlocks.clear();
        if (compound.hasKey("projectedBlocks")) {
            NBTTagList projectedList = compound.getTagList("projectedBlocks", 10);
            for (int i = 0; i < projectedList.tagCount(); i++) {
                NBTTagCompound entry = projectedList.getCompoundTagAt(i);
                this.savedProjectedBlocks.add(new BlockPos(entry.getInteger("x"), entry.getInteger("y"), entry.getInteger("z")));
            }
        }
    }

    @Override
    public int getProjectionSpeed() {
        return MFFSConfig.baseProjectionSpeed + MFFSConfig.speedModuleFactor * getModuleCount(ModModules.SPEED, getUpgradeSlots());
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

    public IBlockState getCamoBlock(Vec3d pos) {
        if (!this.world.isRemote && this.camoModulePresent) {
            if (getModeStack().getItem() instanceof CustomProjectorModeItem custom) {
                Map<Vec3d, IBlockState> map = custom.getFieldBlocks(this, getModeStack());
                IBlockState block = map.get(pos);
                if (block != null) {
                    return block;
                }
            }

            Optional<IBlockState> ownCamo = this.cachedOwnCamo;
            if (ownCamo == null) {
                ownCamo = getAllModuleItemsStream()
                    .mapPartial(ProjectorBlockEntity::getFilterBlock)
                    .findFirst();
                this.cachedOwnCamo = ownCamo;
            }
            if (ownCamo.isPresent()) {
                return ownCamo.get();
            }

            return getWeightedCamoBlockFromNeighbors();
        }
        return null;
    }

    /**
     * Invalidates the per-pass neighbor camo cache so the next call to
     * {@link #getWeightedCamoBlockFromNeighbors()} rescans adjacent inventories.
     * Called at the start of each projection pass.
     */
    private void invalidateNeighborCamoCache() {
        this.cachedNeighborWeightedList = null;
    }

    @Nullable
    private IBlockState getWeightedCamoBlockFromNeighbors() {
        List<IBlockState> weightedList = this.cachedNeighborWeightedList;
        if (weightedList == null) {
            Map<IBlockState, Integer> neighborsInventory = checkNeighbors();
            if (neighborsInventory.isEmpty()) {
                this.cachedNeighborWeightedList = Collections.emptyList();
                return null;
            }
            weightedList = neighborsInventory.entrySet()
                .stream()
                .flatMap(e -> Collections.nCopies(e.getValue(), e.getKey()).stream())
                .collect(Collectors.toList());
            this.cachedNeighborWeightedList = weightedList;
        }
        if (weightedList.isEmpty()) {
            return null;
        }
        return weightedList.get(ThreadLocalRandom.current().nextInt(weightedList.size()));
    }

    private CompletableFuture<?> runCalculationTask() {
        CompletableFuture<List<TargetPosPair>> future = this.semaphore.beginStage(ProjectionStage.CALCULATING);
        CompletableFuture.supplyAsync(this::calculateFieldPositions).whenComplete((result, ex) -> {
            if (ex != null) future.completeExceptionally(ex);
            else future.complete(result);
        });
        return future
            .whenComplete((list, ex) -> {
                if (list != null) {
                    for (Module module : getModuleInstances()) {
                        module.onCalculate(this, list);
                    }
                    Collections.shuffle(list);
                    // Rebuild the fast-lookup set and publish atomically so onSetBlock can do
                    // O(1) membership checks without touching the full TargetPosPair list.
                    HashSet<BlockPos> newSet = new HashSet<>(list.size() * 2, 0.5f);
                    for (TargetPosPair pair : list) newSet.add(pair.pos());
                    this.calculatedFieldSet = Collections.unmodifiableSet(newSet);
                }
            })
            .exceptionally(throwable -> {
                MFFSMod.LOGGER.error("Error calculating force field", throwable);
                return Collections.emptyList();
            });
    }

    /**
     * Gap-fill sweep: iterates the full calculated field geometry and projects any position
     * that is currently projectable (air / liquid / replaceable) but not yet occupied by our
     * force field. This is useful for PvP, not much else.
     *
     * Unlike the async selection path this runs entirely on the main thread, so it reads
     * current world state directly rather than going through the projection cache. The cache
     * is still invalidated for each placed block so the async selection stays coherent.
     */
    private void fillGaps() {
        invalidateNeighborCamoCache();
        IBlockState ffState = ModBlocks.FORCE_FIELD.getDefaultState();
        Set<Module> modules = getModuleInstances();
        for (Module module : modules) {
            module.beforeProject(this);
        }
        int speed = getProjectionSpeed();
        int placed = 0;
        fieldLoop:
        for (TargetPosPair pair : getCalculatedFieldPositions()) {
            if (placed >= speed) break;
            BlockPos pos = pair.pos();
            if (!this.world.isBlockLoaded(pos)) continue;
            // Read current world state directly — always current, no cache race.
            IBlockState current = this.world.getBlockState(pos);
            // Skip positions already holding our force field.
            if (current.getBlock() == ModBlocks.FORCE_FIELD) continue;
            // Also skip positions tracked as projected (prevents re-placing during soft destroy).
            if (this.projectedBlocks.contains(pos)) continue;
            boolean projectable = (current.getBlock().isAir(current, this.world, pos)
                || current.getMaterial().isLiquid()
                || ModTags.getForceFieldReplaceable().contains(current.getBlock())
                || (hasModule(ModModules.DISINTEGRATION) && current.getBlockHardness(this.world, pos) != -1))
                && !pos.equals(this.pos);
            if (!projectable) continue;
            boolean gapModulePlaced = false;
            for (Module m : modules) {
                Module.ProjectAction action = m.onProject(this, pos);
                if (action == Module.ProjectAction.SKIP) continue fieldLoop;
                if (action == Module.ProjectAction.INTERRUPT) return;
                if (action == Module.ProjectAction.PLACED) {
                    gapModulePlaced = true;
                    break;
                }
            }
            if (gapModulePlaced) {
                this.pendingRemoval.remove(pos);
                this.projectedBlocks.add(pos);
                expandFieldSendRadius(pos);
                this.projectionCache.invalidate(pos);
                placed++;
                continue fieldLoop;
            }
            if (!canConsumeFieldCost(1)) return;
            this.world.setBlockState(pos, ffState, 0);
            net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(pos);
            if (te instanceof ForceFieldBlockEntity be) {
                be.setProjector(this.pos);
                be.setCamouflage(getCamoBlock(pair.original()));
            }
            this.world.notifyBlockUpdate(pos, ffState, ffState, 3);
            this.fortronStorage.extractFortron(1, false);
            this.pendingRemoval.remove(pos);
            this.projectedBlocks.add(pos);
            expandFieldSendRadius(pos);
            this.projectionCache.invalidate(pos);
            placed++;
        }
    }

    private List<TargetPosPair> calculateFieldPositions() {
        ProjectorMode mode = getMode().orElseThrow(NoSuchElementException::new);
        Set<Vec3d> fieldPoints = hasModule(ModModules.INVERTER) ? mode.getInteriorPoints(this) : mode.getExteriorPoints(this);
        BlockPos translation = getTranslation();
        int rotationYaw = getRotationYaw();
        int rotationPitch = getRotationPitch();
        int rotationRoll = getRotationRoll();

        return StreamEx.of(fieldPoints)
            .mapToEntry(pos -> rotationYaw != 0 || rotationPitch != 0 || rotationRoll != 0 ? ModUtil.rotateByAngleExact(pos, rotationYaw, rotationPitch, rotationRoll) : pos)
            .mapValues(pos -> pos.add(this.pos.getX(), this.pos.getY(), this.pos.getZ()).add(translation.getX(), translation.getY(), translation.getZ()))
            .filterValues((Vec3d pos) -> pos.y <= this.world.getHeight())
            .mapKeyValue((Vec3d original, Vec3d pos) -> new TargetPosPair(new BlockPos((int) Math.round(pos.x), (int) Math.round(pos.y), (int) Math.round(pos.z)), original))
            .toMutableList();
    }

    private CompletableFuture<?> runSelectionTask() {
        CompletableFuture<List<TargetPosPair>> future = this.semaphore.beginStage(ProjectionStage.SELECTING);
        CompletableFuture.supplyAsync(this::selectProjectablePositions).whenComplete((result, ex) -> {
            if (ex != null) future.completeExceptionally(ex);
            else future.complete(result);
        });
        return future
            .exceptionally(throwable -> {
                MFFSMod.LOGGER.error("Error selecting force field blocks", throwable);
                return Collections.emptyList();
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
        int constructionSpeed = getProjectionSpeed();
        List<TargetPosPair> projectable = new ArrayList<>();
        fieldLoop:
        for (int i = 0, constructionCount = 0; i < fieldToBeProjected.size() && constructionCount < constructionSpeed && !isInvalid() && this.semaphore.isInStage(ProjectionStage.SELECTING); i++) {
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
            if (this.projectionCache.getUnchecked(pos).getValue() && this.world.isBlockLoaded(pos)) {
                projectable.add(pair);
                constructionCount++;
            }
        }
        return projectable;
    }

    public static Optional<IBlockState> getFilterBlock(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock blockItem) {
            Block block = blockItem.getBlock();
            IBlockState defaultState = block.getDefaultState();
            // Invisible blocks (technical/structural) are never valid camo
            if (block.getRenderType(defaultState) == net.minecraft.util.EnumBlockRenderType.INVISIBLE) {
                return Optional.empty();
            }
            // Tile entity blocks (chests, furnaces, etc.) are rejected — the TESR delegate
            // only handles a handful of known cases and non-cube models look broken.
            if (block.hasTileEntity(defaultState)) {
                return Optional.empty();
            }
            // Only allow geometrically full-cube blocks.
            try {
                if (!Block.FULL_BLOCK_AABB.equals(block.getBoundingBox(defaultState, null, BlockPos.ORIGIN))) {
                    return Optional.empty();
                }
            } catch (Exception ignored) {
                // If bounding box lookup crashes (world-dependent shape), conservatively reject.
                return Optional.empty();
            }
            // Preserve item meta value so colour-carrying meta (e.g. stained glass tint) is kept.
            return Optional.of(block.getStateFromMeta(stack.getMetadata()));
        }
        return Optional.empty();
    }

    public Map<IBlockState, Integer> checkNeighbors() {
        Map<IBlockState, Integer> countMap = new HashMap<>();
        if (!this.world.isRemote) {
            for (EnumFacing side : EnumFacing.values()) {
                net.minecraft.tileentity.TileEntity neighbor = this.world.getTileEntity(this.pos.offset(side));
                if (neighbor != null && neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
                    IItemHandler handler = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
                    if (handler != null) {
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack stack = handler.getStackInSlot(i);
                            int count = stack.getCount();
                            getFilterBlock(stack).ifPresent(state -> countMap.put(state, countMap.getOrDefault(state, 0) + count));
                        }
                    }
                }
            }
        }
        return countMap;
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
