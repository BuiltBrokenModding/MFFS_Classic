package dev.su5ed.mffs.util.inventory;

import java.util.function.IntSupplier;

public class SlotInventoryColored extends SlotInventory implements ColoredSlot {
    private final IntSupplier tintColorSupplier;
    
    public SlotInventoryColored(InventorySlot inventorySlot, int x, int y, IntSupplier tintColorSupplier) {
        super(inventorySlot, x, y);
        
        this.tintColorSupplier = tintColorSupplier;
    }

    @Override
    public boolean shouldTint() {
        return true;
    }

    @Override
    public int getTintColor() {
        return this.tintColorSupplier.getAsInt();
    }

    @Override
    public boolean tintItems() {
        return false;
    }
}
