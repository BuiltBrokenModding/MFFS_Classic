package dev.su5ed.mffs.blockentity;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.ObjectCache;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.card.Card;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.Module.ProjectAction;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.item.CustomModeItem;
import dev.su5ed.mffs.item.ModuleItem;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.network.UpdateAnimationSpeed;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModSounds;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.InventorySlot;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.ProjectorCalculationThread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
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

    /**
     * A set containing all positions of all force field blocks.
     */
    protected final Set<BlockPos> forceFields = new HashSet<>();

    protected final Set<BlockPos> calculatedField = Collections.synchronizedSet(new HashSet<>());

    public boolean isCalculating = false;
    public boolean isCalculated = false;
    public int clientAnimationSpeed;

    public ProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), pos, state, 50);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof Card);
        this.projectorModeSlot = addSlot("projectorMode", InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof ProjectorMode);
        ImmutableListMultimap.Builder<Direction, InventorySlot> builder = new ImmutableListMultimap.Builder<>();
        StreamEx.of(Direction.values())
            .forEach(side -> IntStreamEx.range(2)
                .mapToObj(i -> addSlot("field_module_" + side.getName() + "_" + i, InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof Module))
                .forEach(slot -> builder.put(side, slot)));
        this.fieldModuleSlots = builder.build();
        this.upgradeSlots = createUpgradeSlots(6, true);
    }

    public int computeAnimationSpeed() {
        int speed = 4;
        int fortronCost = getFortronCost();
        if (isActive() && getMode() != null && this.fortronStorage.extractFortron(fortronCost, true) >= fortronCost) {
            speed *= fortronCost / 8.0f;
        }
        return Math.min(120, speed);
    }

    public int getAnimationSpeed() {
        return this.clientAnimationSpeed;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        reCalculateForceField();
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
        list.addAll(this.fieldModuleSlots.values());
    }

    // TODO Stablizer Module Construction FXs

    public void onThreadComplete() {
        destroyField();
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
        if (isActive() && getMode() != null && this.fortronStorage.extractFortron(fortronCost, true) >= fortronCost) {
            consumeCost();

            if (getTicks() % 10 == 0) {
                if (!this.isCalculated) {
                    reCalculateForceField();
                } else {
                    projectField();
                }
            }

            if (getTicks() % (2 * 20) == 0 && !hasModule(ModItems.SILENCE_MODULE.get())) {
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

    @Nullable
    @Override
    public ProjectorMode getMode() {
        return getModeStack().getItem() instanceof ProjectorMode mode ? mode : null;
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
            ModuleItem translationModule = ModItems.TRANSLATION_MODULE.get();
            int zTranslationNeg = getModuleCount(translationModule, getSlotsFromSide(Direction.NORTH));
            int zTranslationPos = getModuleCount(translationModule, getSlotsFromSide(Direction.SOUTH));

            int xTranslationNeg = getModuleCount(translationModule, getSlotsFromSide(Direction.WEST));
            int xTranslationPos = getModuleCount(translationModule, getSlotsFromSide(Direction.EAST));

            int yTranslationPos = getModuleCount(translationModule, getSlotsFromSide(Direction.UP));
            int yTranslationNeg = getModuleCount(translationModule, getSlotsFromSide(Direction.DOWN));

            return new BlockPos(xTranslationPos - xTranslationNeg, yTranslationPos - yTranslationNeg, zTranslationPos - zTranslationNeg);
        });
    }

    @Override
    public BlockPos getPositiveScale() {
        return cached(POSITIVE_SCALE_CACHE_KEY, () -> {
            ModuleItem scaleModule = ModItems.SCALE_MODULE.get();
            int zScalePos = getModuleCount(scaleModule, getSlotsFromSide(Direction.SOUTH));
            int xScalePos = getModuleCount(scaleModule, getSlotsFromSide(Direction.EAST));
            int yScalePos = getModuleCount(scaleModule, getSlotsFromSide(Direction.UP));

            int omnidirectionalScale = getModuleCount(scaleModule, getUpgradeSlots());

            zScalePos += omnidirectionalScale;
            xScalePos += omnidirectionalScale;
            yScalePos += omnidirectionalScale;

            return new BlockPos(xScalePos, yScalePos, zScalePos);
        });
    }

    @Override
    public BlockPos getNegativeScale() {
        return cached(NEGATIVE_SCALE_CACHE_KEY, () -> {
            ModuleItem scaleModule = ModItems.SCALE_MODULE.get();
            int zScaleNeg = getModuleCount(scaleModule, getSlotsFromSide(Direction.NORTH));
            int xScaleNeg = getModuleCount(scaleModule, getSlotsFromSide(Direction.WEST));
            int yScaleNeg = getModuleCount(scaleModule, getSlotsFromSide(Direction.DOWN));

            int omnidirectionalScale = getModuleCount(scaleModule, getUpgradeSlots());

            zScaleNeg += omnidirectionalScale;
            xScaleNeg += omnidirectionalScale;
            yScaleNeg += omnidirectionalScale;

            return new BlockPos(xScaleNeg, yScaleNeg, zScaleNeg);
        });
    }

    @Override
    public int getRotationYaw() {
        return cached(ROTATION_YAW_CACHE_KEY, () -> {
            ModuleItem rotationModule = ModItems.ROTATION_MODULE.get();
            int rotation = getModuleCount(rotationModule, getSlotsFromSide(Direction.EAST)) 
                - getModuleCount(rotationModule, getSlotsFromSide(Direction.WEST));
            return rotation * 2;
        });
    }

    @Override
    public int getRotationPitch() {
        return cached(ROTATION_PITCH_CACHE_KEY, () -> {
            ModuleItem rotationModule = ModItems.ROTATION_MODULE.get();
            int rotation = getModuleCount(rotationModule, getSlotsFromSide(Direction.UP)) 
                - getModuleCount(rotationModule, getSlotsFromSide(Direction.DOWN));
            return rotation * 2;
        });
    }

    @Override
    public int getRotationRoll() {
        return cached(ROTATION_ROLL_CACHE_KEY, () -> {
            ModuleItem rotationModule = ModItems.ROTATION_MODULE.get();
            int rotation = getModuleCount(rotationModule, getSlotsFromSide(Direction.SOUTH))
                - getModuleCount(rotationModule, getSlotsFromSide(Direction.NORTH));
            return rotation * 2;
        });
    }

    @Override
    public Set<BlockPos> getCalculatedField() {
        return this.calculatedField;
    }

    @Override
    public Set<BlockPos> getInteriorPoints() {
        return cached(INTERIOR_POINTS_CACHE_KEY, () -> {
            Set<BlockPos> interiorPoints = getMode().getInteriorPoints(this);
            BlockPos translation = getTranslation();
            int rotationYaw = getRotationYaw();
            int rotationPitch = getRotationPitch();
            int rotationRoll = getRotationRoll();
            return StreamEx.of(interiorPoints)
                .map(pos -> rotationYaw != 0 || rotationPitch != 0 || rotationRoll != 0 ? ModUtil.rotateByAngle(pos, rotationYaw, rotationPitch, rotationRoll) : pos)
                .map(pos -> pos.offset(this.worldPosition).offset(translation))
                .toSet();
        });
    }

    @Override
    public void projectField() {
        if (!this.level.isClientSide && this.isCalculated && !this.isCalculating) {
            if (this.forceFields.isEmpty() && getModeStack().getItem() instanceof ObjectCache cache) {
                cache.clearCache();
            }

            int constructionCount = 0;
            int constructionSpeed = Math.min(getProjectionSpeed(), MFFSConfig.COMMON.maxFFGenPerTick.get());

            Set<BlockPos> fieldToBeProjected = new HashSet<>(this.calculatedField);
            if (getModules().stream().anyMatch(m -> m.beforeProject(this, fieldToBeProjected))) {
                return;
            }

            fieldLoop:
            for (BlockPos pos : this.calculatedField) {
                if (fieldToBeProjected.contains(pos)) {
                    if (constructionCount > constructionSpeed) {
                        break;
                    }

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
                        this.level.setBlock(pos, state, Block.UPDATE_ALL);
                        // Set the controlling projector of the force field block to this one
                        this.level.getBlockEntity(pos, ModObjects.FORCE_FIELD_BLOCK_ENTITY.get())
                            .ifPresent(be -> {
                                be.setProjector(this.worldPosition);
                                Block camouflage = getCamoBlock(pos);
                                if (camouflage != null) {
                                    be.setCamouflage(camouflage);
                                }
                            });

                        this.fortronStorage.extractFortron(1, false);
                        this.forceFields.add(pos);
                        constructionCount++;
                    }
                } else {
                    BlockState state = this.level.getBlockState(pos);

                    if (state.getBlock() instanceof ForceFieldBlock forceFieldBlock && forceFieldBlock.getProjector(this.level, pos) == this) {
                        this.level.removeBlock(pos, false);
                    }
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
        return (state.isAir() || getModuleCount(ModItems.DISINTEGRATION_MODULE.get()) > 0 && state.getDestroySpeed(this.level, pos) != -1 || state.getMaterial().isLiquid() || state.is(ModTags.FORCEFIELD_REPLACEABLE))
            && !state.is(ModBlocks.FORCE_FIELD.get()) && !pos.equals(this.worldPosition)
            && this.level.isLoaded(pos);
    }

    @Override
    public void destroyField() {
        if (!this.level.isClientSide && this.isCalculated && !this.isCalculating) {
            StreamEx.of(this.calculatedField)
                .filter(pos -> this.level.getBlockState(pos).is(ModBlocks.FORCE_FIELD.get()))
                .forEach(pos -> this.level.removeBlock(pos, false));
        }

        this.forceFields.clear();
        this.calculatedField.clear();
        this.isCalculated = false;
    }

    @Override
    public int getProjectionSpeed() {
        return 28 + 28 * getModuleCount(ModItems.SPEED_MODULE.get(), getUpgradeSlots());
    }

    private void reCalculateForceField() {
        reCalculateForceField(null);
    }

    private void reCalculateForceField(@Nullable Runnable callBack) {
        if (!this.level.isClientSide && !this.isCalculating && getMode() != null) {
            if (getModeStack().getItem() instanceof ObjectCache cache) {
                cache.clearCache();
            }

            this.forceFields.clear();
            this.calculatedField.clear();

            // Start multi-threading calculation
            new ProjectorCalculationThread(this, callBack).start();
        }
    }

    public Block getCamoBlock(BlockPos pos) {
        if (!this.level.isClientSide && getModuleCount(ModItems.CAMOUFLAGE_MODULE.get()) > 0) {
            if (getMode() instanceof CustomModeItem custom) {
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
}
