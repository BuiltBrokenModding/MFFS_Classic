package dev.su5ed.mffs.blockentity;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.ObjectCache;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.item.ModuleItem;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.CalcUtil;
import dev.su5ed.mffs.util.DelayedEvent;
import dev.su5ed.mffs.util.FrequencyCard;
import dev.su5ed.mffs.util.InventorySlot;
import dev.su5ed.mffs.util.ProjectorCalculationThread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkHooks;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ProjectorBlockEntity extends ModularBlockEntity implements MenuProvider, Projector {
    public final List<DelayedEvent> delayedEvents = new ArrayList<>();
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

    public ProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), pos, state, 50);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof FrequencyCard);
        this.projectorModeSlot = addSlot("projectorMode", InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof ProjectorMode);
        ImmutableListMultimap.Builder<Direction, InventorySlot> builder = new ImmutableListMultimap.Builder<>();
        StreamEx.of(Direction.values())
            .forEach(side -> IntStreamEx.range(2)
                .mapToObj(i -> addSlot("field_module_" + side.getName() + "_" + i, InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof Module))
                .forEach(slot -> builder.put(side, slot)));
        this.fieldModuleSlots = builder.build();
        this.upgradeSlots = IntStreamEx.range(6)
            .mapToObj(i -> addSlot("upgrade_" + i, InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof Module))
            .toList();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        calculateForceField();
    }
    
    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!this.level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, this, this.worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    // TODO Stablizer Module Construction FXs

    public void onThreadComplete() {
        destroyField();
    }

    @Override
    public void tickServer() {
        super.tickServer();

        Iterator<DelayedEvent> it = this.delayedEvents.iterator();
        while (it.hasNext()) {
            DelayedEvent event = it.next();

            if (event.ticks <= 0) {
                it.remove();
            } else {
                event.update();
            }
        }

        int fortronCost = getFortronCost();
        if (isActive() && getMode() != null && requestFortron(fortronCost, IFluidHandler.FluidAction.SIMULATE) >= fortronCost) {
            consumeCost();

            if (getTicks() % 10 == 0) {
                if (!this.isCalculated) {
                    calculateForceField();
                } else {
                    projectField();
                }
            }

            // TODO Sound
//            if (getTicks() % (2 * 20) == 0) {
//                this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, "mffs.field", 0.6f, (1 - this.worldObj.rand.nextFloat() * 0.1f));
//            }
        } else {
            destroyField();
        }
    }

    @Override
    protected void animate() {
        int fortronCost = getFortronCost();
        if (isActive() && getMode() != null && requestFortron(fortronCost, IFluidHandler.FluidAction.SIMULATE) >= fortronCost) {
            this.animation += fortronCost / 3;
        }
    }

    @Override
    public void setRemoved() {
        destroyField();
        super.setRemoved();
    }
    
    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
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
        String cacheID = "getTranslation";

        if (MFFSConfig.COMMON.useCache.get() && getCache(cacheID) instanceof BlockPos pos) {
            return pos;
        }

        ModuleItem translationModule = ModItems.TRANSLATION_MODULE.get();
        int zTranslationNeg = getModuleCount(translationModule, getSlotsFromSide(Direction.NORTH));
        int zTranslationPos = getModuleCount(translationModule, getSlotsFromSide(Direction.SOUTH));

        int xTranslationNeg = getModuleCount(translationModule, getSlotsFromSide(Direction.WEST));
        int xTranslationPos = getModuleCount(translationModule, getSlotsFromSide(Direction.EAST));

        int yTranslationPos = getModuleCount(translationModule, getSlotsFromSide(Direction.UP));
        int yTranslationNeg = getModuleCount(translationModule, getSlotsFromSide(Direction.DOWN));

        BlockPos translation = new BlockPos(xTranslationPos - xTranslationNeg, yTranslationPos - yTranslationNeg, zTranslationPos - zTranslationNeg);

        if (MFFSConfig.COMMON.useCache.get()) {
            putCache(cacheID, translation);
        }

        return translation;
    }

    @Override
    public BlockPos getPositiveScale() {
        String cacheID = "getPositiveScale";

        if (MFFSConfig.COMMON.useCache.get() && getCache(cacheID) instanceof BlockPos pos) {
            return pos;
        }

        ModuleItem scaleModule = ModItems.SCALE_MODULE.get();
        int zScalePos = getModuleCount(scaleModule, getSlotsFromSide(Direction.SOUTH));
        int xScalePos = getModuleCount(scaleModule, getSlotsFromSide(Direction.EAST));
        int yScalePos = getModuleCount(scaleModule, getSlotsFromSide(Direction.UP));

        int omnidirectionalScale = getModuleCount(scaleModule, getUpgradeSlots());

        zScalePos += omnidirectionalScale;
        xScalePos += omnidirectionalScale;
        yScalePos += omnidirectionalScale;

        BlockPos positiveScale = new BlockPos(xScalePos, yScalePos, zScalePos);

        if (MFFSConfig.COMMON.useCache.get()) {
            putCache(cacheID, positiveScale);
        }

        return positiveScale;
    }

    @Override
    public BlockPos getNegativeScale() {
        String cacheID = "getNegativeScale";

        if (MFFSConfig.COMMON.useCache.get() && getCache(cacheID) instanceof BlockPos pos) {
            return pos;
        }

        ModuleItem scaleModule = ModItems.SCALE_MODULE.get();
        int zScaleNeg = getModuleCount(scaleModule, getSlotsFromSide(Direction.NORTH));
        int xScaleNeg = getModuleCount(scaleModule, getSlotsFromSide(Direction.WEST));
        int yScaleNeg = getModuleCount(scaleModule, getSlotsFromSide(Direction.DOWN));

        int omnidirectionalScale = getModuleCount(scaleModule, getUpgradeSlots());

        zScaleNeg += omnidirectionalScale;
        xScaleNeg += omnidirectionalScale;
        yScaleNeg += omnidirectionalScale;

        BlockPos negativeScale = new BlockPos(xScaleNeg, yScaleNeg, zScaleNeg);

        if (MFFSConfig.COMMON.useCache.get()) {
            putCache(cacheID, negativeScale);
        }

        return negativeScale;
    }

    @Override
    public int getRotationYaw() {
        String cacheID = "getRotationYaw";

        if (MFFSConfig.COMMON.useCache.get() && getCache(cacheID) instanceof Integer i) {
            return i;
        }

        ModuleItem rotationModule = ModItems.ROTATION_MODULE.get();
        int horizontalRotation = getModuleCount(rotationModule, getSlotsFromSide(Direction.EAST))
            - getModuleCount(rotationModule, getSlotsFromSide(Direction.WEST))
            + getModuleCount(rotationModule, getSlotsFromSide(Direction.SOUTH))
            - getModuleCount(rotationModule, getSlotsFromSide(Direction.NORTH));

        if (MFFSConfig.COMMON.useCache.get()) {
            putCache(cacheID, horizontalRotation);
        }

        return horizontalRotation;
    }

    @Override
    public int getRotationPitch() {
        String cacheID = "getRotationPitch";

        if (MFFSConfig.COMMON.useCache.get() && getCache(cacheID) instanceof Integer i) {
            return i;
        }

        ModuleItem rotationModule = ModItems.ROTATION_MODULE.get();
        int verticleRotation = getModuleCount(rotationModule, getSlotsFromSide(Direction.UP))
            - getModuleCount(rotationModule, getSlotsFromSide(Direction.DOWN));

        if (MFFSConfig.COMMON.useCache.get()) {
            putCache(cacheID, verticleRotation);
        }

        return verticleRotation;
    }

    @Override
    public Set<BlockPos> getCalculatedField() {
        return this.calculatedField;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<BlockPos> getInteriorPoints() {
        String cacheID = "getInteriorPoints";

        if (MFFSConfig.COMMON.useCache.get() && getCache(cacheID) instanceof Set<?> set) {
            return (Set<BlockPos>) set;
        }

        Set<BlockPos> interiorPoints = getMode().getInteriorPoints(this);
        BlockPos translation = getTranslation();
        int rotationYaw = getRotationYaw();
        int rotationPitch = getRotationPitch();
        Set<BlockPos> newField = StreamEx.of(interiorPoints)
            .map(pos -> rotationYaw != 0 || rotationPitch != 0 ? CalcUtil.rotateByAngle(pos, rotationYaw, rotationPitch) : pos)
            .map(pos -> pos.offset(this.worldPosition).offset(translation))
            .toSet();

        if (MFFSConfig.COMMON.useCache.get()) {
            putCache(cacheID, newField);
        }

        return newField;
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
            if (getModules().stream().anyMatch(m -> m.onProject(this, fieldToBeProjected))) {
                return;
            }

            Iterator<BlockPos> it = this.calculatedField.iterator();

            fieldLoop:
            while (it.hasNext()) {
                BlockPos pos = it.next();

                if (fieldToBeProjected.contains(pos)) {
                    if (constructionCount > constructionSpeed) {
                        break;
                    }

                    BlockState state = this.level.getBlockState(pos);
                    if (state.isAir() || getModuleCount(ModItems.DISINTEGRATION_MODULE.get()) > 0 && state.getDestroySpeed(this.level, pos) != -1 || state.getMaterial().isLiquid() || state.is(ModTags.FORCEFIELD_REPLACEABLE)) {
                        // Prevents the force field projector from disintegrating itself.
                        if (!state.is(ModBlocks.FORCE_FIELD.get()) && !pos.equals(this.worldPosition)) {
                            if (this.level.isLoaded(pos)) {
                                for (Module module : getModules()) {
                                    int flag = module.onProject(this, pos);

                                    if (flag == 1) {
                                        continue fieldLoop;
                                    } else if (flag == 2) {
                                        break fieldLoop;
                                    }
                                }

                                this.level.setBlock(pos, ModBlocks.FORCE_FIELD.get().defaultBlockState(), Block.UPDATE_ALL);

                                // Sets the controlling projector of the force field block to
                                // this one.
                                BlockEntity be = this.level.getBlockEntity(pos);

                                if (be instanceof ForceFieldBlockEntity forceFieldBlockEntity) {
                                    forceFieldBlockEntity.setProjector(this.worldPosition);
                                }

                                requestFortron(1, IFluidHandler.FluidAction.EXECUTE);
                                this.forceFields.add(pos);
                                constructionCount++;
                            }
                        }
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
    public void destroyField() {
        if (!this.level.isClientSide && this.isCalculated && !this.isCalculating) {
            Set<BlockPos> copiedSet = new HashSet<>(this.calculatedField);

            for (BlockPos pos : copiedSet) {
                BlockState state = this.level.getBlockState(pos);

                if (state.is(ModBlocks.FORCE_FIELD.get())) {
                    this.level.removeBlock(pos, false);
                }
            }
        }

        this.forceFields.clear();
        this.calculatedField.clear();
        this.isCalculated = false;
    }

    @Override
    public int getProjectionSpeed() {
        return 28 + 28 * getModuleCount(ModItems.SPEED_MODULE.get(), getUpgradeSlots());
    }

    private void calculateForceField() {
        calculateForceField(null);
    }

    private void calculateForceField(@Nullable Runnable callBack) {
        if (!this.level.isClientSide && !this.isCalculating) {
            if (getMode() != null) {
                if (getModeStack().getItem() instanceof ObjectCache cache) {
                    cache.clearCache();
                }

                this.forceFields.clear();
                this.calculatedField.clear();

                // Start multi-threading calculation
                new ProjectorCalculationThread(this, callBack).start();
            }
        }
    }
}
