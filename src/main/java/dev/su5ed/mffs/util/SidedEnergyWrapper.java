package dev.su5ed.mffs.util;

import net.neoforged.neoforge.transfer.energy.DelegatingEnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SidedEnergyWrapper extends DelegatingEnergyHandler {
    private final boolean canExtract;
    private final boolean canReceive;

    public SidedEnergyWrapper(EnergyHandler wrapped, boolean canExtract, boolean canReceive) {
        super(wrapped);
        this.canExtract = canExtract;
        this.canReceive = canReceive;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (!this.canReceive) {
            return 0;
        }
        return super.insert(amount, transaction);
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (!this.canExtract) {
            return 0;
        }
        return super.extract(amount, transaction);
    }
}
