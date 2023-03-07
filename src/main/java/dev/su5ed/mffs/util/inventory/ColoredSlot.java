package dev.su5ed.mffs.util.inventory;

public interface ColoredSlot {
    boolean shouldTint();

    int getTintColor();

    boolean tintItems();
}
