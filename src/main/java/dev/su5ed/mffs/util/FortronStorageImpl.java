package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.setup.ModFluids;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FortronStorageImpl implements FortronStorage {
    private final BlockEntity owner;
    private final FluidTank fortronTank;
    private final Runnable onContentsChanged;

    private int frequency;

    public FortronStorageImpl(BlockEntity owner, int capacity, Runnable onContentsChanged) {
        this.owner = owner;
        this.fortronTank = new FortronFluidTank(capacity);
        this.onContentsChanged = onContentsChanged;
    }

    public FluidTank getFortronTank() {
        return this.fortronTank;
    }

    public void setCapacity(int capacity) {
        this.fortronTank.setCapacity(capacity);
        if (!this.fortronTank.isEmpty()) {
            this.fortronTank.getFluid().setAmount(Math.min(this.fortronTank.getFluidAmount(), capacity));
        }
    }

    @Override
    public BlockEntity getOwner() {
        return this.owner;
    }

    @Override
    public int getStoredFortron() {
        return this.fortronTank.getFluidAmount();
    }

    @Override
    public void setStoredFortron(int energy) {
        this.fortronTank.setFluid(Fortron.getFortron(energy));
    }

    @Override
    public int getFortronCapacity() {
        return this.fortronTank.getCapacity();
    }

    @Override
    public int extractFortron(int ml, boolean simulate) {
        return this.fortronTank.drain(ml, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE).getAmount();
    }

    @Override
    public int insertFortron(int ml, boolean simulate) {
        return this.fortronTank.fill(Fortron.getFortron(ml), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void serialize(ValueOutput valueOutput) {
        valueOutput.putChild("fortronTank", this.fortronTank);
        valueOutput.putInt("frequency", this.frequency);
    }

    @Override
    public void deserialize(ValueInput valueInput) {
        valueInput.child("fortronTank").ifPresent(this.fortronTank::deserialize);
    }

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public void setFrequency(int frequency) {
        this.frequency = frequency;
        this.owner.setChanged();
    }

    private class FortronFluidTank extends FluidTank {
        public FortronFluidTank(int capacity) {
            super(capacity);
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.FORTRON_FLUID.get();
        }

        @Override
        protected void onContentsChanged() {
            FortronStorageImpl.this.onContentsChanged.run();
        }
    }
}
