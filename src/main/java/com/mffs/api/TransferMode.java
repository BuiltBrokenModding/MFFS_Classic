package com.mffs.api;

/**
 * @author Calclavia
 */
public enum TransferMode {
    EQUALIZE, DISTRIBUTE, DRAIN, FILL;

    public static final String[] NAME_NORMALIZED = {
            "Equalize", "Distribute", "Drain", "Fill"
    };

    public TransferMode toggle() {
        int newOrdinal = ordinal() + 1;

        if (newOrdinal >= values().length) {
            newOrdinal = 0;
        }
        return values()[newOrdinal];
    }
}
