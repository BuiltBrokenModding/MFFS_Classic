package dev.su5ed.mffs.setup;

/**
 * 1.12.2 GUI ID constants for NetworkRegistry.INSTANCE.registerGuiHandler().
 * Used in player.openGui(MFFSMod.INSTANCE, id, world, x, y, z)
 * and mapped to screens in MFFSGuiHandler.
 */
public final class GuiIds {
    public static final int COERCION_DERIVER      = 0;
    public static final int FORTRON_CAPACITOR     = 1;
    public static final int INTERDICTION_MATRIX   = 2;
    public static final int BIOMETRIC_IDENTIFIER  = 3;
    public static final int PROJECTOR             = 4;
    public static final int REMOTE_CONTROLLER     = 5;

    private GuiIds() {}
}
