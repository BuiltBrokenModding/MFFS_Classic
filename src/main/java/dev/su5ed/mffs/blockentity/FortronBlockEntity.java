package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.BiometricIdentifierLink;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FortronStorageImpl;
import dev.su5ed.mffs.util.FrequencyGrid;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.TransferMode;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class FortronBlockEntity extends InventoryBlockEntity implements BiometricIdentifierLink, Activatable {
    public final InventorySlot frequencySlot;

    public final FortronStorageImpl fortronStorage;
    private final LazyOptional<FortronStorage> fortronCap;
    private final LazyOptional<IFluidHandler> fluidCap;

    private boolean markSendFortron = true;
    private boolean active;
    protected int animation;

    protected FortronBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.fortronStorage = new FortronStorageImpl(this, getBaseFortronTankCapacity() * FluidType.BUCKET_VOLUME, this::setChanged);
        this.fortronCap = LazyOptional.of(() -> this.fortronStorage);
        this.fluidCap = LazyOptional.of(this.fortronStorage::getFortronTank);
        this.frequencySlot = addSlot("frequency", InventorySlot.Mode.BOTH, ModUtil::isCard, this::onFrequencySlotChanged);
    }

    public int getAnimation() {
        return this.animation;
    }

    public void setMarkSendFortron(boolean markSendFortron) {
        this.markSendFortron = markSendFortron;
    }

    /**
     * @return The initial fortron tank capacity in buckets
     */
    public int getBaseFortronTankCapacity() {
        return 1;
    }

    protected List<ItemStack> getCards() {
        return List.of(this.frequencySlot.getItem());
    }

    protected void animate() {
        if (isActive()) {
            this.animation++;
        }
    }

    protected void onFrequencySlotChanged(ItemStack stack) {
        if (stack.getItem() instanceof FrequencyCard frequencyCard) {
            frequencyCard.setFrequency(stack, this.fortronStorage.getFrequency());
        }
    }

    @Override
    public boolean isActive() {
        return this.level.isClientSide ? getBlockState().getValue(BaseEntityBlock.ACTIVE) : this.active || this.level.hasNeighborSignal(this.worldPosition);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        FrequencyGrid.instance().register(this.fortronStorage);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        FrequencyGrid.instance().unregister(this.fortronStorage);
    }

    @Override
    public void tickServer() {
        super.tickServer();

        BlockState state = getBlockState();
        boolean active = isActive();
        if (state.getValue(BaseEntityBlock.ACTIVE) != active) {
            this.level.setBlock(this.worldPosition, state.setValue(BaseEntityBlock.ACTIVE, active), Block.UPDATE_ALL);
        }
    }

    @Override
    public void tickClient() {
        super.tickClient();

        animate();
    }

    @Override
    public void setRemoved() {
        if (this.markSendFortron) {
            // Let remaining Fortron escape.
            Fortron.transferFortron(this.fortronStorage, FrequencyGrid.instance().getFortronBlocks(this.level, this.worldPosition, 100, this.fortronStorage.getFrequency()), TransferMode.DRAIN, Integer.MAX_VALUE);
        }

        super.setRemoved();
    }

    @Override
    protected void loadCommonTag(CompoundTag tag) {
        super.loadCommonTag(tag);

        this.fortronStorage.deserializeNBT(tag.getCompound("fortronStorage"));
        this.active = tag.getBoolean("active");
    }

    @Override
    protected void saveCommonTag(CompoundTag tag) {
        super.saveCommonTag(tag);

        tag.put("fortronStorage", this.fortronStorage.serializeNBT());
        tag.putBoolean("active", this.active);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.FORTRON) {
            return this.fortronCap.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.fluidCap.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Gets the first linked security station, based on the card slots and frequency.
     */
    @Override
    public BiometricIdentifier getBiometricIdentifier() {
        return getBiometricIdentifiers().stream()
            .findFirst()
            .orElse(null);
    }

    @Override
    public Set<BiometricIdentifier> getBiometricIdentifiers() {
        return StreamEx.of(getCards())
            .mapPartial(stack -> {
                if (stack.getItem() instanceof CoordLink link) {
                    BlockPos linkedPos = link.getLink(stack);
                    if (linkedPos != null) {
                        BlockEntity be = this.level.getBlockEntity(linkedPos);
                        if (be instanceof BiometricIdentifier biometricIdentifier) {
                            return Optional.of(biometricIdentifier);
                        }
                    }
                }
                return Optional.empty();
            })
            .append(StreamEx.of(FrequencyGrid.instance().get(this.fortronStorage.getFrequency()))
                .select(BiometricIdentifier.class))
            .toSet();
    }
}
