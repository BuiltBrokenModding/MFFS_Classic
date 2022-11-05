package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.fortron.FortronFrequency;
import dev.su5ed.mffs.api.fortron.FrequencyGrid;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.TransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class FortronBlockEntity extends AnimatedBlockEntity implements FortronFrequency {
    protected final FluidTank fortronTank = new FluidTank(FluidType.BUCKET_VOLUME);
    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> this.fortronTank);

    private boolean markSendFortron = true;

    protected FortronBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (this.markSendFortron) {
            // Let remaining Fortron escape.
            Fortron.transferFortron(this, FrequencyGrid.instance().getFortronTiles(this.level, this.worldPosition, 100, getFrequency()), TransferMode.DRAIN, Integer.MAX_VALUE);
        }
    }

    @Override
    protected void saveTag(CompoundTag tag) {
        super.saveTag(tag);
        
        tag.put("fortronTank", this.fortronTank.writeToNBT(new CompoundTag()));
    }

    @Override
    protected void loadTag(CompoundTag tag) {
        super.loadTag(tag);
        
        this.fortronTank.readFromNBT(tag.getCompound("fortronTank"));
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
    public void setFortronEnergy(int joules) {
        this.fortronTank.setFluid(Fortron.getFortron(joules));
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
}
