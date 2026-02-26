package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.setup.ModFluids;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class FortronStorageImpl implements FortronStorage {
    private final BlockEntity owner;
    private final FortronFluidTank fortronTank;
    private final Runnable onContentsChanged;

    private int frequency;

    public FortronStorageImpl(BlockEntity owner, int capacity, Runnable onContentsChanged) {
        this.owner = owner;
        this.fortronTank = new FortronFluidTank(capacity);
        this.onContentsChanged = onContentsChanged;
    }

    public FluidStacksResourceHandler getFortronTank() {
        return this.fortronTank;
    }

    public void setCapacity(int capacity) {
        this.fortronTank.setCapacity(capacity);
        if (!this.fortronTank.isEmpty()) {
            this.fortronTank.setAmount(Math.min(this.fortronTank.getFluidAmount(), capacity));
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
        this.fortronTank.setAmount(energy);
    }

    @Override
    public int getFortronCapacity() {
        return this.fortronTank.getCapacity();
    }

    @Override
    public int extractFortron(int ml, Transaction tx) {
        return this.fortronTank.extract(ml, tx);
    }

    @Override
    public int insertFortron(int ml, Transaction tx) {
        return this.fortronTank.insert(ml, tx);
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

    private class FortronFluidTank extends FluidStacksResourceHandler {
        private final FluidResource resource = FluidResource.of(ModFluids.FORTRON_FLUID);

        public FortronFluidTank(int capacity) {
            super(1, capacity);
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public boolean isEmpty() {
            return getResource(0).isEmpty();
        }

        public void setAmount(int amount) {
            this.set(0, this.resource, amount);
        }

        public int getFluidAmount() {
            return getAmountAsInt(0);
        }
        
        public int getCapacity() {
            return this.capacity;
        }

        public int insert(int amount, TransactionContext transaction) {
            return super.insert(0, this.resource, amount, transaction);
        }

        public int extract(int amount, TransactionContext transaction) {
            return super.extract(0, this.resource, amount, transaction);
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.is(ModFluids.FORTRON_FLUID);
        }

        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            FortronStorageImpl.this.onContentsChanged.run();
        }
    }
}
