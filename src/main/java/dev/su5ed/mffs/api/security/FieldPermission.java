package dev.su5ed.mffs.api.security;

public enum FieldPermission {
    /**
     * Allows a player to sneak-walk through force fields.
     */
    WARP_SNEAK,
    /**
     * Allows a player to walk through force fields.
     */
    WARP_WALK,
    /**
     * Allows block access and opening GUIs.
     */
    USE_BLOCKS,
    /**
     * Allows breaking and placing blocks.
     */
    PLACE_BLOCKS,
    /**
     * Allows configuring biometric identifiers.
     */
    CONFIGURE_SECURITY_CENTER,
    /**
     * Allows bypassing the identity matrix.
     */
    BYPASS_DEFENSE,
    /**
     * Allows bypassing item confiscation.
     */
    BYPASS_CONFISCATION,
    /**
     * Remote Control - Allows player to access blocks using a remote controller.
     */
    REMOTE_CONTROL
}
