package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.api.FrequencyBlock;
import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.fortron.FortronFrequency;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.fortron.FrequencyGrid;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.BiometricIdentifierLink;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.network.ToggleModePacketClient;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FrequencyCard;
import dev.su5ed.mffs.util.InventorySlot;
import dev.su5ed.mffs.util.TransferMode;
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
import net.minecraftforge.fluids.capability.templates.FluidTank;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class FortronBlockEntity extends InventoryBlockEntity implements FortronFrequency, FortronStorage, FrequencyBlock, BiometricIdentifierLink, Activatable {
    protected final FluidTank fortronTank = new FluidTank(getBaseFortronTankCapacity() * FluidType.BUCKET_VOLUME) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };
    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> this.fortronTank);
    public final InventorySlot frequencySlot;

    protected int frequency;
    public boolean markSendFortron = true;
    private boolean active;
    private int animation;

    protected FortronBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.frequencySlot = addSlot("frequency", InventorySlot.Mode.NONE, stack -> stack.getItem() instanceof FrequencyCard);
    }

    public long getAnimation() {
        return this.animation;
    }

    /**
     * @return The initial fortron tank capacity in buckets
     */
    public int getBaseFortronTankCapacity() {
        return 1;
    }

    @Override
    public boolean isActive() {
        return this.active || this.level.hasNeighborSignal(this.worldPosition);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        FrequencyGrid.instance().register(this);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        FrequencyGrid.instance().unregister(this);
    }

    @Override
    public void tickServer() {
        super.tickServer();

        BlockState state = getBlockState();
        boolean active = isActive();
        if (state.getValue(BaseEntityBlock.ACTIVE) != active) {
            this.level.setBlock(this.worldPosition, state.setValue(BaseEntityBlock.ACTIVE, active), Block.UPDATE_ALL);

            if (!this.level.isClientSide()) {
                sendToChunk(new ToggleModePacketClient(this.worldPosition, active));
            }
        }
    }

    @Override
    public void tickClient() {
        super.tickClient();

        if (isActive()) this.animation++;
    }

    @Override
    public void setRemoved() {
        if (this.markSendFortron) {
            // Let remaining Fortron escape.
            Fortron.transferFortron(this, FrequencyGrid.instance().getFortronTiles(this.level, this.worldPosition, 100, getFrequency()), TransferMode.DRAIN, Integer.MAX_VALUE);
        }
        
        super.setRemoved();
    }

    @Override
    protected void saveTag(CompoundTag tag) {
        super.saveTag(tag);

        tag.put("fortronTank", this.fortronTank.writeToNBT(new CompoundTag()));
        tag.putInt("frequency", this.frequency);
    }

    @Override
    protected void loadTag(CompoundTag tag) {
        super.loadTag(tag);

        this.fortronTank.readFromNBT(tag.getCompound("fortronTank"));
        this.frequency = tag.getInt("frequency");
    }

    @Override
    protected void loadCommonTag(CompoundTag tag) {
        this.active = tag.getBoolean("active");
    }

    @Override
    protected void saveCommonTag(CompoundTag tag) {
        tag.putBoolean("active", this.active);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.fluidCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public void setFortronEnergy(int fe) {
        this.fortronTank.setFluid(Fortron.getFortron(fe));
    }

    @Override
    public int getFortronEnergy() {
        return this.fortronTank.getFluidAmount();
    }

    @Override
    public int getFortronCapacity() {
        return this.fortronTank.getCapacity();
    }

    @Override
    public int requestFortron(int joules, IFluidHandler.FluidAction action) {
        return this.fortronTank.drain(joules, action).getAmount();
    }

    @Override
    public int provideFortron(int joules, IFluidHandler.FluidAction action) {
        return this.fortronTank.fill(Fortron.getFortron(joules), action);
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
            .append(StreamEx.of(FrequencyGrid.instance().get(getFrequency()))
                .select(BiometricIdentifier.class))
            .toSet();
    }

    protected List<ItemStack> getCards() {
        return List.of(this.frequencySlot.getItem());
    }
}
