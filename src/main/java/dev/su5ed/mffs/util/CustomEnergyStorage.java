package dev.su5ed.mffs.util;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.BooleanSupplier;

public class CustomEnergyStorage extends SimpleEnergyHandler {
    private final BooleanSupplier canReceive;
    private final Runnable onChanged;

    public CustomEnergyStorage(int capacity, int maxTransfer, BooleanSupplier canReceive, Runnable onChanged) {
        super(capacity, maxTransfer);

        this.canReceive = canReceive;
        this.onChanged = onChanged;
    }

    @Override
    protected void onEnergyChanged(int previousAmount) {
        super.onEnergyChanged(previousAmount);
        this.onChanged.run();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (!this.canReceive.getAsBoolean()) {
            return 0;
        }
        return super.insert(amount, transaction);
    }

    public void setMaxTransfer(int maxTransfer) {
        this.maxInsert = maxTransfer;
        this.maxExtract = maxTransfer;
    }

    public int getRequestedEnergy() {
        return getCapacityAsInt() - getAmountAsInt();
    }
    
    public boolean canExtract(int extract) {
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = extract(extract, tx);
            return extracted >= extract;
        }
    }
}
