package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.BiometricIdentifierLink;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.*;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class FortronBlockEntity extends InventoryBlockEntity implements BiometricIdentifierLink, Activatable {
    public final InventorySlot frequencySlot;
    public final FortronStorageImpl fortronStorage;

    private boolean markSendFortron = true;
    private boolean active;
    protected int animation;

    protected FortronBlockEntity() {
        super();
        this.fortronStorage = new FortronStorageImpl(this, getBaseFortronTankCapacity(), this::markDirty);
        this.frequencySlot = addSlot("frequency", InventorySlot.Mode.BOTH, ModUtil::isCard, this::onFrequencySlotChanged);
    }

    public int getAnimation() {
        return this.animation;
    }

    public void setMarkSendFortron(boolean markSendFortron) {
        this.markSendFortron = markSendFortron;
    }

    /** @return The initial Fortron tank capacity in F */
    public int getBaseFortronTankCapacity() {
        return 1000;
    }

    protected List<ItemStack> getCards() {
        return java.util.Collections.singletonList(this.frequencySlot.getItem());
    }

    protected void animate() {
        if (isActive()) this.animation++;
    }

    protected void onFrequencySlotChanged(ItemStack stack) {
        FrequencyCard card = stack.getCapability(ModCapabilities.FREQUENCY_CARD, null);
        if (card != null) {
            card.setFrequency(this.fortronStorage.getFrequency());
        }
    }

    @Override
    public boolean isActive() {
        if (this.world.isRemote) {
            return this.world.getBlockState(this.pos).getValue(BaseEntityBlock.ACTIVE);
        }
        return this.active || this.world.isBlockPowered(this.pos);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        markDirty();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        FrequencyGrid.instance(this.world.isRemote).register(this.fortronStorage);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        FrequencyGrid.instance(this.world.isRemote).unregister(this.fortronStorage);
    }

    @Override
    public void invalidate() {
        if (this.world != null) {
            FrequencyGrid.instance(this.world.isRemote).unregister(this.fortronStorage);
        }
        super.invalidate();
    }

    @Override
    public void tickServer() {
        super.tickServer();

        // Guard: if we somehow missed onLoad() (chunk edge-case) re-register on the first tick
        // so the FrequencyGrid is always populated with the correct, NBT-loaded frequency.
        // I don't actually know if this solved the strange issues I was having, but oh well.
        if (getTicks() == 1 && !FrequencyGrid.instance(this.world.isRemote).get().contains(this.fortronStorage)) {
            FrequencyGrid.instance(this.world.isRemote).register(this.fortronStorage);
        }

        boolean active = isActive();
        net.minecraft.block.state.IBlockState state = this.world.getBlockState(this.pos);
        if (state.getValue(BaseEntityBlock.ACTIVE) != active) {
            this.world.setBlockState(this.pos, state.withProperty(BaseEntityBlock.ACTIVE, active));
        }
    }

    @Override
    public void tickClient() {
        super.tickClient();
        animate();
    }

    /**
     * Called before the tile entity is removed from the world.
     * Transfers remaining Fortron to nearby tiles.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos) {
        if (this.markSendFortron) {
            Fortron.transferFortron(this.fortronStorage, FrequencyGrid.instance(this.world.isRemote).get(this.world, this.pos, 100, this.fortronStorage.getFrequency()), TransferMode.DRAIN, Integer.MAX_VALUE);
        }
    }

    // -------------------------------------------------------------------------
    // Capability exposure: FortronStorage
    // -------------------------------------------------------------------------

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ModCapabilities.FORTRON) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ModCapabilities.FORTRON) {
            return (T) this.fortronStorage;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected void loadCommonTag(NBTTagCompound compound) {
        super.loadCommonTag(compound);
        if (compound.hasKey("fortronStorage")) {
            this.fortronStorage.readNbt(compound.getCompoundTag("fortronStorage"));
        }
        this.active = compound.getBoolean("active");
    }

    @Override
    protected void saveCommonTag(NBTTagCompound compound) {
        super.saveCommonTag(compound);
        NBTTagCompound fortronTag = new NBTTagCompound();
        this.fortronStorage.writeNbt(fortronTag);
        compound.setTag("fortronStorage", fortronTag);
        compound.setBoolean("active", this.active);
    }

    @Override
    public BiometricIdentifier getBiometricIdentifier() {
        return getBiometricIdentifiers().stream().findFirst().orElse(null);
    }

    @Override
    public Set<BiometricIdentifier> getBiometricIdentifiers() {
        return StreamEx.of(getCards())
            .mapPartial(stack -> {
                if (stack.getItem() instanceof CoordLink link) {
                    BlockPos linkPos = link.getLink(stack);
                    if (linkPos != null) {
                        net.minecraft.tileentity.TileEntity te = this.world.getTileEntity(linkPos);
                        if (te != null) {
                            BiometricIdentifier bi = te.getCapability(ModCapabilities.BIOMETRIC_IDENTIFIER, null);
                            return Optional.ofNullable(bi);
                        }
                    }
                }
                return Optional.empty();
            })
            .append(StreamEx.of(FrequencyGrid.instance(this.world.isRemote).get(this.fortronStorage.getFrequency()))
                .mapPartial(storage -> {
                    net.minecraft.tileentity.TileEntity te = storage.getOwner();
                    return Optional.ofNullable(te != null ? te.getCapability(ModCapabilities.BIOMETRIC_IDENTIFIER, null) : null);
                }))
            .toSet();
    }
}
