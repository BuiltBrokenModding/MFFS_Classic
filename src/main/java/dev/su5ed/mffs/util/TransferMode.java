package dev.su5ed.mffs.util;

/**
 * The force field transfer mode.
 */
public enum TransferMode {
    EQUALIZE,
    DISTRIBUTE,
    DRAIN,
    FILL;

    private static final TransferMode[] VALUES = values();
    
    public TransferMode next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }
}
