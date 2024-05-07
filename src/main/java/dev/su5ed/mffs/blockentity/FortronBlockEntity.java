package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.card.FrequencyCard;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class FortronBlockEntity extends InventoryBlockEntity implements BiometricIdentifierLink, Activatable {
    public final InventorySlot frequencySlot;

    public final FortronStorageImpl fortronStorage;

    private boolean markSendFortron = true;
    private boolean active;
    protected int animation;

    protected FortronBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.fortronStorage = new FortronStorageImpl(this, getBaseFortronTankCapacity() * FluidType.BUCKET_VOLUME, this::setChanged);
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
        FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD);
        if (card != null) {
            card.setFrequency(this.fortronStorage.getFrequency());
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
            this.level.setBlockAndUpdate(this.worldPosition, state.setValue(BaseEntityBlock.ACTIVE, active));
        }
    }

    @Override
    public void tickClient() {
        super.tickClient();

        animate();
    }

    @Override
    public void beforeBlockRemove() {
        super.beforeBlockRemove();
        if (this.markSendFortron) {
            // Let remaining Fortron escape.
            Fortron.transferFortron(this.fortronStorage, FrequencyGrid.instance().get(this.level, this.worldPosition, 100, this.fortronStorage.getFrequency()), TransferMode.DRAIN, Integer.MAX_VALUE);
        }
        this.level.invalidateCapabilities(this.worldPosition);
    }

    @Override
    protected void loadCommonTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadCommonTag(tag, provider);

        this.fortronStorage.deserializeNBT(provider, tag.getCompound("fortronStorage"));
        this.active = tag.getBoolean("active");
    }

    @Override
    protected void saveCommonTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveCommonTag(tag, provider);

        tag.put("fortronStorage", this.fortronStorage.serializeNBT(provider));
        tag.putBoolean("active", this.active);
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
                    return Optional.ofNullable(link.getLink(stack))
                        .map(this.level::getBlockEntity)
                        .map(be -> be.getLevel().getCapability(ModCapabilities.BIOMETRIC_IDENTIFIER, be.getBlockPos(), be.getBlockState(), be, null));
                }
                return Optional.empty();
            })
            .append(StreamEx.of(FrequencyGrid.instance().get(this.fortronStorage.getFrequency()))
                .mapPartial(storage -> {
                    BlockEntity be = storage.getOwner();
                    return Optional.ofNullable(be.getLevel().getCapability(ModCapabilities.BIOMETRIC_IDENTIFIER, be.getBlockPos(), be.getBlockState(), be, null));
                }))
            .toSet();
    }
}
