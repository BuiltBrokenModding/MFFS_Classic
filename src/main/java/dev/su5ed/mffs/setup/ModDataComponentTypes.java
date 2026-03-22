package dev.su5ed.mffs.setup;

// =============================================================================
// 1.12.2 Backport: Item data storage
// =============================================================================

/**
 * Data storage constants for MFFS items (1.12.2 NBT-based approach).
 */
public final class ModDataComponentTypes {

    // NBT sub-tag root for all MFFS item data
    public static final String ROOT_TAG = "mffs";

    // Remote Controller link position
    public static final String NBT_LINK_POS = "linkPos"; // stored as long (BlockPos.asLong)

    // Battery energy storage
    public static final String NBT_ENERGY = "energy";

    // Frequency Card
    public static final String NBT_CARD_FREQUENCY = "frequency";

    // Identification Card
    public static final String NBT_ID_CARD_PROFILE     = "profile";     // GameProfile NBT
    public static final String NBT_ID_CARD_PERMISSIONS = "permissions"; // NBTTagList of strings

    // Custom Projector Mode
    public static final String NBT_PATTERN_ID       = "patternId";
    public static final String NBT_STRUCTURE_COORDS = "structureCoords";
    public static final String NBT_STRUCTURE_MODE   = "structureMode";

    private ModDataComponentTypes() {}
}
