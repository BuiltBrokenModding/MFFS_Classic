package dev.su5ed.mffs;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModModules;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MFFSConfig {

    private static Configuration configuration;
    private static final Logger LOGGER = LogManager.getLogger(MFFSConfig.class);

    /** Number of ticks between each Fortron network transfer cycle. */
    public static final int FORTRON_TRANSFER_TICKS = 10;

    // -------------------------------------------------------------------------
    // Client
    // -------------------------------------------------------------------------
    /** Apply a fancy glitch effect on projector mode renders. */
    public static boolean enableProjectorModeGlitch = true;
    /** If enabled and CodeChickenLib is installed, render an emissive accent overlay on machine blocks (deriver, projector, capacitor, biometric identifier). */
    public static boolean enableCodeChickenLibEmissiveBlocks          = true;
    /** How many deferred world.checkLight() calls to process per client tick when the Glow Module is active. */
    public static int glowLightChecksPerTick = 60;

    // -------------------------------------------------------------------------
    // General
    // -------------------------------------------------------------------------
    /** Turning this to false will make MFFS run without electricity or energy systems required. */
    public static boolean enableElectricity            = true;
    /** Cache allows temporary data saving to decrease calculations required. */
    public static boolean useCache                     = true;
    /** Allow server operators to bypass Force Field biometry. */
    public static boolean allowOpBiometryOverride      = true;
    /** Should the interdiction matrix interact with creative players? */
    public static boolean interactCreative             = true;
    /** Give players a copy of the MFFS guidebook when they first join a world. */
    public static boolean giveGuidebookOnFirstJoin     = false;
    /** Disable Steel Ingot and Steel Compound item registration (other mods likely provide these). */
    public static boolean disableSteelItems            = true;

    // -------------------------------------------------------------------------
    // Coercion Deriver
    // -------------------------------------------------------------------------
    /** FE to convert into 1 Fortron. */
    public static int coercionDriverFePerFortron                  = 400;
    /**
     * Percentage discount applied to the FE cost per Fortron for each installed Speed Module (0–100).
     * Example: a value of 5 means each Speed Module reduces the cost by 5% (4 modules = 20% cheaper).
     */
    public static int coercionDriverFePerFortronSpeedDiscount     = 1;
    /** FE to subtract when converting Fortron to FE. */
    public static int coercionDriverFortronToFeLoss               = 1;
    /** Base limit of Fortron produced per second (F/s). Divided by 20 internally to get per-tick value. */
    public static int coercionDriverFortronPerSecond                = 1000;
    /** Production bonus per speed module per second (F/s). Divided by 20 internally to get per-tick value. */
    public static int coercionDriverFortronPerSecondSpeedModule     = 500;
    /** Initial Fortron tank capacity in F. */
    public static int coercionDriverInitialTankCapacity             = 30000;
    /** Fortron tank capacity added per Capacity Module in F. */
    public static int coercionDriverTankCapacityPerModule           = 5000;
    /**
     * Per-item catalyst definitions loaded from config at startup.
     * See {@link CatalystEntry} for the config format.
     */
    public static List<CatalystEntry> catalystEntries = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Fortron Capacitor
    // -------------------------------------------------------------------------
    /** Initial Fortron tank capacity in F. */
    public static int fortronCapacitorInitialTankCapacity           = 700000;
    /** Fortron tank capacity added per Capacity Module in F. */
    public static int fortronCapacitorTankCapacityPerModule         = 10000;
    /** Base Fortron transmission rate in F/s. Converted to F/cycle internally. */
    public static int fortronCapacitorInitialTransmissionRate       = 500;
    /** Additional Fortron transmission rate per Speed Module in F/s. */
    public static int fortronCapacitorTransmissionRatePerModule     = 100;
    /** Initial transmission range in blocks. */
    public static int fortronCapacitorInitialRange                  = 15;
    /** Additional transmission range per Scale Module in blocks. */
    public static int fortronCapacitorRangePerModule                = 1;
    // -------------------------------------------------------------------------
    /** Energy to consume when the Interdiction Matrix kills a player. */
    public static int    interdictionMatrixKillEnergy = 0;
    /** Fortron to consume when the Interdiction Matrix kills a mob (hostile or friendly). */
    public static int    interdictionMatrixMobKillEnergy = 0;
    /** Minimum warning zone buffer (in blocks) beyond the action range, even with no Warn Modules. */
    public static int    interdictionMatrixMinWarnRange = 6;
    /**
     * How often (in ticks) the Interdiction Matrix runs zone actions (confiscation, damage, etc.).
     * Example: 20 fires once per second; 10 is twice as responsive but doubles server load.
     */
    public static int    interdictionMatrixActionTickRate = 20;
    /** Initial Fortron tank capacity in F. */
    public static int    interdictionMatrixInitialTankCapacity   = 500000;
    /** Fortron tank capacity added per Capacity Module in F. */
    public static int    interdictionMatrixTankCapacityPerModule = 5000;
    /** Controls what happens to mob drops when the Interdiction Matrix kills a mob. */
    public static InterdictionDropMode interdictionMobDropMode = InterdictionDropMode.COLLECTION_OPTIONAL;

    // -------------------------------------------------------------------------
    // Projector
    // -------------------------------------------------------------------------
    /** Base number of blocks projected per tick before any speed modules are applied. Example: 7 places 7 blocks per tick with no modules installed. */
    public static int     baseProjectionSpeed          = 7;
    /**
     * Blocks added to projection speed per Speed Module per tick.
     * Formula: baseProjectionSpeed + speedModuleFactor * speedModules.
     * Example: with baseProjectionSpeed=7 and speedModuleFactor=1, four Speed Modules gives 11 blocks/tick.
     */
    public static int     speedModuleFactor            = 1;
    /** Divides the speed-module count when computing the removal drain rate.
     *  Formula: baseProjectionSpeed + speedModuleFactor * (speedModules / drainSpeedFactor). Higher = slower removal per speed module. */
    public static int     drainSpeedFactor             = 2;
    /** Maximum scale factor for the custom Force Field shape mode. Example: 200 allows the custom field to span up to 200 blocks. */
    public static int     maxCustomModeScale           = 200;
    /** Maximum total number of Speed Modules insertable into a Force Field Projector. Example: 64 permits a full stack across the upgrade slots. */
    public static int     maxSpeedModulesProjector     = 64;
    /** Maximum total number of Warn Modules insertable into an Interdiction Matrix. Example: 64 permits a full stack across the upgrade slots. */
    public static int     maxWarnModulesIM             = 64;
    /**
     * How often (in ticks) to run the main async projection cycle (calculate + select + project).
     * Example: 10 runs it every 0.5 s; 20 runs it once per second.
     */
    public static int projectionCycleTicks = 1;
    /**
     * When true, the projector sweeps its entire calculated field every projection cycle and
     * immediately fills any position that became projectable (e.g. terrain dug out under the
     * field). Gaps fill within one projectionCycleTicks interval instead of 10-20 seconds.
     * Disable on servers where the extra per-tick iteration over large fields is undesirable.
     */
    public static boolean enableFastFill = false;
    /** Initial Fortron tank capacity in F. */
    public static int projectorInitialTankCapacity   = 500000;
    /** Fortron tank capacity added per Capacity Module in F. */
    public static int projectorTankCapacityPerModule = 50000;

    // -------------------------------------------------------------------------
    // Shock Module (behaviour — enable/cost are in the Modules section)
    // -------------------------------------------------------------------------
    /**
     * Damage dealt per Shock Module per collision, in half-hearts.
     * Fractional values are allowed. Example: 0.25 deals a quarter-heart per module (1 heart at 4 modules).
     */
    public static float shockModuleDamagePerModule = 0.25F;
    /**
     * Maximum allowed value for {@link #shockModuleKnockbackStrengthPerModule} as set in config.
     * Raise this if you need extremely high knockback for modpack purposes (absolute ceiling is 1000).
     */
    public static float maxShockModuleKnockbackStrength = 10.0F;
    /**
     * Knockback strength applied per Shock Module per collision when {@link #doShockModuleKnockback} is enabled.
     * The impulse pushes the entity away from the force field block.
     * Fractional values are allowed (e.g. 0.25). Must be between 0 and {@link #maxShockModuleKnockbackStrength}.
     * Example: 0.25 with 8 modules gives a total strength of 2.0.
     */
    public static float shockModuleKnockbackStrengthPerModule = 0.5F;
    /**
     * When true, apply a knockback impulse on every Shock Module collision in addition to (or instead of) damage.
     * The impulse direction is away from the force field block.
     */
    public static boolean doShockModuleKnockback = true;
    /**
     * When true, replaces the normal directional knockback with a near-vertical launch (~85° from horizontal).
     * The entity is sent almost straight up with only a tiny outward nudge — pure chaos mode.
     * Requires {@link #doShockModuleKnockback} to also be true.
     */
    public static boolean doSpace = false;

    // -------------------------------------------------------------------------
    // Anti-module Damage Rates (behaviour — enable/cost are in the Modules section)
    // -------------------------------------------------------------------------
    /**
     * Damage dealt per second by the Anti-Personnel module to non-authorized players (half-hearts).
     * Each action tick applies {@code damagePerSecond × (actionTickRate / 20)} damage.
     * Example: 0.5 deals 1 half-heart per second at the default 20-tick action rate.
     * <p>Balance: at the default 0.5 ❤/s an unarmoured player survives ~20 seconds —
     * threatening without being instakill. Values above ~2.0 kill an unarmoured player
     * within ~5 s; values above 100 effectively restore the original instakill behaviour.
     * Lowering below 0.1 renders the module a nuisance rather than a deterrent.
     */
    public static float antiPersonnelDamagePerSecond = 0.5F;
    /**
     * Damage dealt per second by the Anti-Friendly module to passive mobs (half-hearts).
     * Each action tick applies {@code damagePerSecond × (actionTickRate / 20)} damage.
     * Example: 0.5 deals 1 half-heart per second at the default 20-tick action rate.
     * <p>Balance: at the default 0.5 ❤/s most animals (10–20 ❤) die in 20–40 s —
     * gradual attrition rather than instant clearance. Raising above 5.0 effectively
     * turns the module into a rapid area-clear which may unbalance passive-mob farms
     * near the field.
     */
    public static float antiFriendlyDamagePerSecond = 0.5F;
    /**
     * Damage dealt per second by the Anti-Hostile module to hostile mobs (half-hearts).
     * Each action tick applies {@code damagePerSecond × (actionTickRate / 20)} damage.
     * Example: 0.5 deals 1 half-heart per second at the default 20-tick action rate.
     * <p>Balance: at the default 0.5 ❤/s even low-health hostiles (zombies, 20 ❤) take
     * ~40 s to die — providing attrition over time. Raising above 5.0 makes the area
     * lethal within a few seconds, effectively creating an automated mob-kill zone.
     */
    public static float antiHostileDamagePerSecond = 0.5F;

    // -------------------------------------------------------------------------
    // Module Slot Limits
    // -------------------------------------------------------------------------
    /** Maximum number of Shock Modules that can be inserted across all Force Field Projector upgrade slots combined (1–64). */
    public static int shockModuleMaxSlotCount = 64;
    /** Maximum number of Anti-Friendly Modules that can be inserted across all Interdiction Matrix upgrade slots combined (1–64). */
    public static int antiFriendlyModuleMaxSlotCount = 64;
    /** Maximum number of Anti-Hostile Modules that can be inserted across all Interdiction Matrix upgrade slots combined (1–64). */
    public static int antiHostileModuleMaxSlotCount = 64;
    /** Maximum number of Anti-Personnel Modules that can be inserted across all Interdiction Matrix upgrade slots combined (1–64). */
    public static int antiPersonnelModuleMaxSlotCount = 64;

    // -------------------------------------------------------------------------
    // Force Field
    // -------------------------------------------------------------------------
    /** Remove confusion and slowness effects for authorized players passing through force fields. */
    public static boolean disableForceFieldEffectsForAuthorizedPlayers = false;
    /** Allow authorized players to walk through force fields without sneaking. */
    public static boolean allowWalkThroughForceFields                  = false;
    /** Spacing used for force field light sources: 1 = every block, 4 = ~1/4 of blocks emit light. */
    public static int forceFieldLightSpacing = 3;
    /** When enabled, only place real lights on force fields touching physical blocks (with spacing applied). */
    public static boolean simpleLighting = true;

    // -------------------------------------------------------------------------
    // Modules (per-module enable/disable + fortron cost)
    // -------------------------------------------------------------------------
    private static final Map<String, Boolean> moduleEnabled = new LinkedHashMap<>();
    private static final Map<String, Float> moduleFortronCost = new LinkedHashMap<>();

    public static boolean isModuleEnabled(String name) {
        return moduleEnabled.getOrDefault(name, true);
    }

    public static float getModuleFortronCost(String name) {
        return moduleFortronCost.getOrDefault(name, 0.5F);
    }

    private static void loadModuleConfig(String name, float defaultCostPerSecond, String description) {
        moduleEnabled.put(name, configuration.getBoolean(name, "modules", true, "Enable " + description));
        moduleFortronCost.put(name, configuration.getFloat(name + "_fortron_cost", "modules",
            defaultCostPerSecond, 0F, 10000F, "Fortron cost per second (F/s) for " + description));
    }

    /**
     * Variant that allows a negative minimum cost, enabling "discount" modules.
     * The total machine cost is clamped to 0 via {@code consumeCost()}, so negative
     * module costs reduce upkeep but never cause Fortron generation.
     */
    private static void loadModuleConfig(String name, float defaultCostPerSecond, float minCost, String description) {
        moduleEnabled.put(name, configuration.getBoolean(name, "modules", true, "Enable " + description));
        moduleFortronCost.put(name, configuration.getFloat(name + "_fortron_cost", "modules",
            defaultCostPerSecond, minCost, 10000F, "Fortron cost per second (F/s) for " + description));
    }

    private static void applyModuleCosts() {
        applyModuleCost(ModModules.FUSION, "fusion_module");
        applyModuleCost(ModModules.SHOCK, "shock_module");
        applyModuleCost(ModModules.SPEED, "speed_module");
        applyModuleCost(ModModules.CAMOUFLAGE, "camouflage_module");
        applyModuleCost(ModModules.SCALE, "scale_module");
        applyModuleCost(ModModules.CAPACITY, "capacity_module");
        applyModuleCost(ModModules.DISINTEGRATION, "disintegration_module");
        applyModuleCost(ModModules.TRANSLATION, "translation_module");
        applyModuleCost(ModModules.ROTATION, "rotation_module");
        applyModuleCost(ModModules.GLOW, "glow_module");
        applyModuleCost(ModModules.SILENCE, "silence_module");
        applyModuleCost(ModModules.SPONGE, "sponge_module");
        applyModuleCost(ModModules.DOME, "dome_module");
        applyModuleCost(ModModules.COLLECTION, "collection_module");
        applyModuleCost(ModModules.STABILIZAZION, "stabilization_module");
        applyModuleCost(ModModules.INVERTER, "inverter_module");
        applyModuleCost(ModModules.WARN, "warn_module");
        applyModuleCost(ModModules.BLOCK_ACCESS, "block_access_module");
        applyModuleCost(ModModules.BLOCK_ALTER, "block_alter_module");
        applyModuleCost(ModModules.ANTI_FRIENDLY, "anti_friendly_module");
        applyModuleCost(ModModules.ANTI_HOSTILE, "anti_hostile_module");
        applyModuleCost(ModModules.ANTI_PERSONNEL, "anti_personnel_module");
        applyModuleCost(ModModules.ANTI_SPAWN, "anti_spawn_module");
        applyModuleCost(ModModules.CONFISCATION, "confiscation_module");
    }

    private static void applyModuleCost(ModuleType<?> type, String name) {
        if (type instanceof ModModules.BaseModuleType<?>) {
            // Config values are in Fortron/second; stored as F/s — billing divides by 20 at extraction time.
            ((ModModules.BaseModuleType<?>) type).setFortronCost(getModuleFortronCost(name));
        }
    }

    // =========================================================================
    // Catalyst helpers
    // =========================================================================

    /**
     * Returns the {@link CatalystEntry} matching the given stack, or {@code null} if the
     * item is not a configured catalyst.
     */
    @Nullable
    public static CatalystEntry getCatalystEntry(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        for (CatalystEntry entry : catalystEntries) {
            if (entry.matches(stack)) return entry;
        }
        return null;
    }

    /**
     * A single catalyst definition parsed from the {@code coercion_deriver.catalysts} config list.
     * <p>
     * Config string format: {@code itemNamespace:itemPath:meta:burnTicks:multiplier}<br>
    * Example: {@code minecraft:quartz:0:200:1.0}
     * <ul>
     *   <li>{@code meta}       — item metadata / damage value (0 for most items)</li>
     *   <li>{@code burnTicks}  — how long one item burns in ticks</li>
     *   <li>{@code multiplier} — additional Fortron output fraction while active (e.g., 0.5 = +50%)</li>
     * </ul>
     */
    public static class CatalystEntry {
        public final String itemId;
        public final int    meta;
        public final int    burnTicks;
        public final double multiplier;

        public CatalystEntry(String itemId, int meta, int burnTicks, double multiplier) {
            this.itemId     = itemId;
            this.meta       = meta;
            this.burnTicks  = burnTicks;
            this.multiplier = multiplier;
        }

        /** Returns true if this entry matches the given item stack. */
        public boolean matches(ItemStack stack) {
            if (stack == null || stack.isEmpty()) return false;
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.itemId));
            if (item == null) return false;
            return stack.getItem() == item && stack.getMetadata() == this.meta;
        }

        /**
         * Creates an {@link ItemStack} for this catalyst entry, or {@code null} if the item
         * is not registered (e.g. a mod that provided it is no longer loaded).
         */
        @Nullable
        public ItemStack toItemStack() {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.itemId));
            if (item == null) return null;
            return new ItemStack(item, 1, this.meta);
        }

        /**
         * Parses a config string of the form {@code namespace:path:meta:burnTicks:multiplier}.
         * Returns {@code null} and logs a warning on malformed input.
         */
        @Nullable
        public static CatalystEntry parse(String s) {
            // Format: namespace:path:meta:burnTicks:multiplier → 5 colon-separated tokens
            String[] parts = s.trim().split(":");
            if (parts.length != 5) {
                LOGGER.warn("[MFFS] Invalid catalyst entry '{}' — expected namespace:path:meta:burnTicks:multiplier", s);
                return null;
            }
            try {
                String itemId    = parts[0] + ":" + parts[1];
                int    meta      = Integer.parseInt(parts[2]);
                int    burnTicks = Integer.parseInt(parts[3]);
                double mult      = Double.parseDouble(parts[4]);
                return new CatalystEntry(itemId, meta, burnTicks, mult);
            } catch (NumberFormatException e) {
                LOGGER.warn("[MFFS] Failed to parse catalyst entry '{}': {}", s, e.getMessage());
                return null;
            }
        }

        @Override
        public String toString() {
            return itemId + ":" + meta + ":" + burnTicks + ":" + multiplier;
        }
    }

    // =========================================================================
    // Load / Save
    // =========================================================================

    /**
     * Load the configuration from disk. Call from {@link MFFSMod#preInit}:
     * <pre>MFFSConfig.load(event.getSuggestedConfigurationFile());</pre>
     */
    public static void load(File configFile) {
        configuration = new Configuration(configFile);
        configuration.load();

        // -- Client [_client] (best-effort; Configuration does not distinguish client/common in 1.12.2) --
        enableProjectorModeGlitch = configuration.getBoolean("enableProjectorModeGlitch", "_client", enableProjectorModeGlitch,
            "Apply a fancy glitch effect on projector mode renders. Reload resources to apply change.");
        enableCodeChickenLibEmissiveBlocks = configuration.getBoolean("enableCodeChickenLibEmissiveBlocks", "_client",
            enableCodeChickenLibEmissiveBlocks,
            "If enabled and CodeChickenLib is installed, render an emissive blue-accent overlay on machine blocks when active.");
        glowLightChecksPerTick = configuration.getInt("glowLightChecksPerTick", "_client", glowLightChecksPerTick, 1, Integer.MAX_VALUE,
            "How many deferred world.checkLight() calls to process per client tick when the Glow Module is active.\n"
            + "Lower = less lighting stutter; too low may cause dark patches.");

        // -- General --
        enableElectricity = configuration.getBoolean("enableElectricity", "_general", enableElectricity,
            "Turning this to false will make MFFS run without electricity or energy systems required. Great for vanilla!");
        useCache = configuration.getBoolean("useCache", "_general", useCache,
            "Cache allows temporary data saving to decrease calculations required");
        allowOpBiometryOverride = configuration.getBoolean("allowOpBiometryOverride", "_general", allowOpBiometryOverride,
            "Allow server operators to bypass Force Field biometry");
        interactCreative = configuration.getBoolean("interactCreative", "_general", interactCreative,
            "Should the interdiction matrix interact with creative players?");
        giveGuidebookOnFirstJoin = configuration.getBoolean("giveGuidebookOnFirstJoin", "_general", giveGuidebookOnFirstJoin,
            "Give players a copy of the MFFS guidebook when they first join a world");
        disableSteelItems = configuration.getBoolean("disableSteelItems", "_general", disableSteelItems,
            "Disable Steel Ingot and Steel Compound item registration. Other mods likely provide these items, causing conflicts.");

        // -- Coercion Deriver --
        coercionDriverFePerFortron = configuration.getInt("feCostPerFortron", "coercion_deriver", coercionDriverFePerFortron, 1, Integer.MAX_VALUE,
            "FE to convert into 1 Fortron");
        coercionDriverFePerFortronSpeedDiscount = configuration.getInt("fePerFortronSpeedDiscount", "coercion_deriver",
            coercionDriverFePerFortronSpeedDiscount, 0, 100,
            "Percentage discount applied to the FE cost per Fortron for each installed Speed Module (0–100).\n"
            + "Example: a value of 5 means each module reduces FE cost by 5% (4 modules = 20% cheaper).");
        coercionDriverFortronToFeLoss = configuration.getInt("fortronToFeLoss", "coercion_deriver", coercionDriverFortronToFeLoss, 0, Integer.MAX_VALUE,
            "FE to subtract when converting Fortron to FE");
        coercionDriverFortronPerSecond = configuration.getInt("fortronPerSecond", "coercion_deriver", coercionDriverFortronPerSecond, 1, Integer.MAX_VALUE,
            "Base Fortron production rate (F/s). Scales with speed modules and catalyst.");
        coercionDriverFortronPerSecondSpeedModule = configuration.getInt("fortronPerSecondSpeedModule", "coercion_deriver",
            coercionDriverFortronPerSecondSpeedModule, 1, Integer.MAX_VALUE,
            "Production bonus per speed module (F/s).");
        coercionDriverInitialTankCapacity = configuration.getInt("initialTankCapacity", "coercion_deriver",
            coercionDriverInitialTankCapacity, 1, Integer.MAX_VALUE,
            "Initial Fortron tank capacity in F.");
        coercionDriverTankCapacityPerModule = configuration.getInt("tankCapacityPerModule", "coercion_deriver",
            coercionDriverTankCapacityPerModule, 0, Integer.MAX_VALUE,
            "Fortron tank capacity added per Capacity Module in F.");
        String[] catalystDefaults = {
            "minecraft:quartz:0:200:1.0",
            "minecraft:redstone:0:200:0.5",
            "minecraft:dye:4:400:0.5"
        };
        String[] catalystStrings = configuration.getStringList("catalysts", "coercion_deriver", catalystDefaults,
            "Catalyst items for the Coercion Deriver.\n"
            + "Format: itemNamespace:itemPath:meta:burnTicks:multiplier\n"
            + "  meta       = item metadata value (0 for most items; lapis lazuli is dye meta 4 so minecraft:dye:4)\n"
            + "  burnTicks  = how long one item burns (in ticks)\n"
            + "  multiplier = additional Fortron output fraction while burning (e.g. 0.5 = +50%)\n"
            + "Examples: Nether Quartz (200t, +100%), Redstone (200t, +50%), Lapis Lazuli (400t, +50%)");
        catalystEntries = new ArrayList<>();
        for (String s : catalystStrings) {
            CatalystEntry entry = CatalystEntry.parse(s);
            if (entry != null) catalystEntries.add(entry);
        }

        // -- Fortron Capacitor --
        fortronCapacitorInitialTankCapacity = configuration.getInt("initialTankCapacity", "fortron_capacitor",
            fortronCapacitorInitialTankCapacity, 1, Integer.MAX_VALUE,
            "Initial Fortron tank capacity in F.");
        fortronCapacitorTankCapacityPerModule = configuration.getInt("tankCapacityPerModule", "fortron_capacitor",
            fortronCapacitorTankCapacityPerModule, 0, Integer.MAX_VALUE,
            "Fortron tank capacity added per Capacity Module in F.");
        fortronCapacitorInitialTransmissionRate = configuration.getInt("initialTransmissionRate", "fortron_capacitor",
            fortronCapacitorInitialTransmissionRate, 1, Integer.MAX_VALUE,
            "Base Fortron transmission rate in F/s. Example: 500 distributes up to 500 F per second across the network.");
        fortronCapacitorTransmissionRatePerModule = configuration.getInt("transmissionRatePerModule", "fortron_capacitor",
            fortronCapacitorTransmissionRatePerModule, 0, Integer.MAX_VALUE,
            "Additional Fortron transmission rate per Speed Module in F/s. Example: 100 means four Speed Modules add 400 F/s to the base rate.");
        fortronCapacitorInitialRange = configuration.getInt("initialRange", "fortron_capacitor",
            fortronCapacitorInitialRange, 1, Integer.MAX_VALUE,
            "Initial transmission range in blocks.");
        fortronCapacitorRangePerModule = configuration.getInt("rangePerModule", "fortron_capacitor",
            fortronCapacitorRangePerModule, 0, Integer.MAX_VALUE,
            "Additional transmission range per Scale Module in blocks.");

        // -- Interdiction Matrix --
        interdictionMatrixKillEnergy = configuration.getInt("interdictionMatrixKillEnergy", "interdiction_matrix", interdictionMatrixKillEnergy, 0, Integer.MAX_VALUE,
            "Fortron to consume when the Interdiction Matrix kills a player");
        interdictionMatrixMobKillEnergy = configuration.getInt("interdictionMatrixMobKillEnergy", "interdiction_matrix", interdictionMatrixMobKillEnergy, 0, Integer.MAX_VALUE,
            "Fortron to consume when the Interdiction Matrix kills a mob (hostile or friendly). Example: 100 costs 100 F per kill.");
        interdictionMatrixMinWarnRange = configuration.getInt("minWarnRange", "interdiction_matrix", interdictionMatrixMinWarnRange, 0, Integer.MAX_VALUE,
            "Minimum warning zone buffer in blocks beyond the action range, applied even with no Warn Modules installed.\n"
            + "Example: 6 extends the warn boundary 6 blocks past the action zone.");
        interdictionMatrixActionTickRate = configuration.getInt("actionTickRate", "interdiction_matrix", interdictionMatrixActionTickRate, 1, 200,
            "How often (in ticks) the Interdiction Matrix runs zone actions such as confiscation and damage.\n"
            + "Lower = more responsive but higher server load. Example: 10 fires every 0.5 s; 20 fires every 1 s.");
        interdictionMatrixInitialTankCapacity = configuration.getInt("initialTankCapacity", "interdiction_matrix",
            interdictionMatrixInitialTankCapacity, 1, Integer.MAX_VALUE,
            "Initial Fortron tank capacity in F.");
        interdictionMatrixTankCapacityPerModule = configuration.getInt("tankCapacityPerModule", "interdiction_matrix",
            interdictionMatrixTankCapacityPerModule, 0, Integer.MAX_VALUE,
            "Fortron tank capacity added per Capacity Module in F.");
        String dropModeStr = configuration.getString("mobDropMode", "interdiction_matrix",
            InterdictionDropMode.NORMAL.name(),
            "Controls what happens to mob drops when the Interdiction Matrix kills a mob.\n"
            + "NORMAL = drops as normal, no collection feature.\n"
            + "COLLECTION_OPTIONAL = mob drops normally, but Collection Module routes drops to an adjacent chest (overflow voided).\n"
            + "COLLECTION_REQUIRED = no drops unless a Collection Module is installed (overflow voided).\n"
            + "DISABLED = no drops at all.",
            InterdictionDropMode.names());
        try {
            interdictionMobDropMode = InterdictionDropMode.valueOf(dropModeStr);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid interdictionMobDropMode '{}', defaulting to NORMAL", dropModeStr);
            interdictionMobDropMode = InterdictionDropMode.NORMAL;
        }

        // -- Projector --
        baseProjectionSpeed = configuration.getInt("baseProjectionSpeed", "projector", baseProjectionSpeed, 1, Integer.MAX_VALUE,
            "Base number of blocks the projector places per tick before speed modules are counted. Example: 7 places 7 blocks/tick with no modules.");
        speedModuleFactor = configuration.getInt("speedModuleFactor", "projector", speedModuleFactor, 1, Integer.MAX_VALUE,
            "Blocks added to projection speed per Speed Module per tick.\n"
            + "Formula: baseProjectionSpeed + speedModuleFactor * speedModules.\n"
            + "Example: with baseProjectionSpeed=7 and speedModuleFactor=1, four modules gives 11 blocks/tick.");
        drainSpeedFactor = configuration.getInt("drainSpeedFactor", "projector", drainSpeedFactor, 1, Integer.MAX_VALUE,
            "Divides the speed-module count when computing the removal drain rate. Formula: baseProjectionSpeed + speedModuleFactor * (speedModules / drainSpeedFactor). Higher values slow down removal per speed module.");
        maxCustomModeScale = configuration.getInt("maxCustomModeScale", "projector", maxCustomModeScale, 0, Integer.MAX_VALUE,
            "Maximum scale factor for the custom Force Field shape mode. Example: 200 allows the custom field to span up to 200 blocks.");
        maxSpeedModulesProjector = configuration.getInt("maxSpeedModulesProjector", "projector", maxSpeedModulesProjector, 0, Integer.MAX_VALUE,
            "Maximum total number of Speed Modules insertable into a Force Field Projector. Example: 64 permits a full stack across the upgrade slots.");
        maxWarnModulesIM = configuration.getInt("maxWarnModulesIM", "interdiction_matrix", maxWarnModulesIM, 0, Integer.MAX_VALUE,
            "Maximum total number of Warn Modules insertable into an Interdiction Matrix. Example: 64 permits a full stack across the upgrade slots.");
        projectionCycleTicks = configuration.getInt("projectionCycleTicks", "projector", projectionCycleTicks, 1, 100,
            "How often (in ticks) the main async projection cycle runs (calculate + select + project).\n"
            + "Example: 10 runs the cycle every 0.5 s; 20 runs it once per second.\n"
            + "It's advised to change projection speed and speed module factor first for best performance.");
        enableFastFill = configuration.getBoolean("enableFastFill", "projector", enableFastFill,
            "When true, the projector sweeps its full calculated field each projection cycle and fills gaps immediately (e.g. terrain dug out under the field). "
            + "Useful in PvP scenarios. Disable on servers with large fields to reduce per-tick overhead.");
        projectorInitialTankCapacity = configuration.getInt("initialTankCapacity", "projector",
            projectorInitialTankCapacity, 1, Integer.MAX_VALUE,
            "Initial Fortron tank capacity in F.");
        projectorTankCapacityPerModule = configuration.getInt("tankCapacityPerModule", "projector",
            projectorTankCapacityPerModule, 0, Integer.MAX_VALUE,
            "Fortron tank capacity added per Capacity Module in F.");

        // -- Force Field --
        disableForceFieldEffectsForAuthorizedPlayers = configuration.getBoolean("disableForceFieldEffectsForAuthorizedPlayers", "force_field",
            disableForceFieldEffectsForAuthorizedPlayers,
            "Remove confusion and slowness effects for authorized players passing through force fields");
        allowWalkThroughForceFields = configuration.getBoolean("allowWalkThroughForceFields", "force_field", allowWalkThroughForceFields,
            "Allow authorized players to walk through force fields without sneaking. WARNING: May cause occasional clipping issues on horizontal platforms.");
        forceFieldLightSpacing = configuration.getInt("forceFieldLightSpacing", "force_field", forceFieldLightSpacing, 1, Integer.MAX_VALUE,
            "Controls spacing for force field light sources. 1 = every block emits, 3 = ~1/3 of blocks.");
        simpleLighting = configuration.getBoolean("simpleLighting", "force_field", simpleLighting,
            "When enabled, real light sources are only placed on force field blocks that touch physical blocks.\n"
            + "The remaining blocks use luminosity to create an illusion of lighting, reducing light-source count.\n"
            + "Example: a large flat field only lights its ground edge rather than every block in the field.");

        // -- Modules (per-module enable/disable + fortron cost) --
        loadModuleConfig("fusion_module",           20.0F,  "Fusion Module");
        loadModuleConfig("shock_module",            20.0F,  "Shock Module");
        shockModuleDamagePerModule = configuration.getFloat("shock_module_damage_per_module", "modules",
            shockModuleDamagePerModule, 0F, 1000F,
            "Damage dealt per Shock Module per collision (half-hearts). Fractional values allowed.\n"
            + "Example: 0.25 means 4 modules deal 1 half-heart; set to 0 for knockback-only.");
        maxShockModuleKnockbackStrength = configuration.getFloat("shock_module_max_knockback_strength", "modules",
            maxShockModuleKnockbackStrength, 0F, 1000F,
            "Upper bound for shock_module_knockback_strength. Raise this to allow stronger knockback values.\n"
            + "Example: 10.0 allows knockback up to 10 per module.");
        shockModuleKnockbackStrengthPerModule = configuration.getFloat("shock_module_knockback_strength", "modules",
            shockModuleKnockbackStrengthPerModule, 0F, maxShockModuleKnockbackStrength,
            "Knockback strength per Shock Module when shock_module_do_knockback is enabled.\n"
            + "The impulse pushes the entity directly away from the force field block.\n"
            + "Fractional values are allowed (e.g. 0.25). Must be between 0 and shock_module_max_knockback_strength.\n"
            + "Example: 0.25 with 8 modules gives a total strength of 2.0.");
        doShockModuleKnockback = configuration.getBoolean("shock_module_do_knockback", "modules", doShockModuleKnockback,
            "When true, apply a knockback impulse on contact in addition to (or instead of) damage.\n"
            + "Example: true with shock_module_damage_per_module > 0 deals both damage and pushes the entity away.");
        doSpace = configuration.getBoolean("shock_module_do_space", "modules", doSpace,
            "When true (and shock_module_do_knockback is also true), the knockback becomes a near-vertical launch (~85 degrees).\n"
            + "The entity is sent almost straight up with only a tiny outward nudge. Pure chaos. Default: false.");
        shockModuleMaxSlotCount = configuration.getInt("shock_module_max_slot_count", "modules",
            shockModuleMaxSlotCount, 1, 64,
            "Maximum number of Shock Modules insertable across all Force Field Projector upgrade slots combined (1–64).");
        loadModuleConfig("speed_module",            20.0F,  "Speed Module");
        loadModuleConfig("camouflage_module",       30.0F,  "Camouflage Module");
        loadModuleConfig("scale_module",            24.0F,  "Scale Module");
        loadModuleConfig("capacity_module",         10.0F,  "Capacity Module");
        loadModuleConfig("disintegration_module",  400.0F,  "Disintegration Module");
        loadModuleConfig("translation_module",      32.0F,  "Translation Module");
        loadModuleConfig("rotation_module",          2.0F,  "Rotation Module");
        loadModuleConfig("glow_module",             10.0F,  "Glow Module");
        loadModuleConfig("silence_module",          20.0F,  "Silence Module");
        loadModuleConfig("sponge_module",           20.0F,  "Sponge Module");
        loadModuleConfig("dome_module",             10.0F,  "Dome Module");
        loadModuleConfig("collection_module",      300.0F,  "Collection Module");
        loadModuleConfig("stabilization_module",   400.0F,  "Stabilization Module");
        loadModuleConfig("inverter_module",        300.0F,  "Inverter Module");
        loadModuleConfig("warn_module",              -2.0F, -10000F, "Warn Module");
        loadModuleConfig("block_access_module",    200.0F,  "Block Access Module");
        loadModuleConfig("block_alter_module",     300.0F,  "Block Alter Module");
        loadModuleConfig("anti_friendly_module",    10.0F,  "Anti-Friendly Module");
        antiFriendlyDamagePerSecond = configuration.getFloat("anti_friendly_damage_per_second", "modules",
            antiFriendlyDamagePerSecond, 0F, 10000F,
            "Damage per second dealt to passive mobs (half-hearts). Applied each action tick proportional to actionTickRate.\n"
            + "Example: 0.5 gives gradual attrition; >5.0 clears the area rapidly and may unbalance nearby passive-mob farms.");
        antiFriendlyModuleMaxSlotCount = configuration.getInt("anti_friendly_module_max_slot_count", "modules",
            antiFriendlyModuleMaxSlotCount, 1, 64,
            "Maximum number of Anti-Friendly Modules insertable across all Interdiction Matrix upgrade slots combined (1–64).");
        loadModuleConfig("anti_hostile_module",     10.0F,  "Anti-Hostile Module");
        antiHostileDamagePerSecond = configuration.getFloat("anti_hostile_damage_per_second", "modules",
            antiHostileDamagePerSecond, 0F, 10000F,
            "Damage per second dealt to hostile mobs (half-hearts). Applied each action tick proportional to actionTickRate.\n"
            + "Example: 0.5 provides slow attrition; >5.0 creates a near-instant kill zone around the matrix.");
        antiHostileModuleMaxSlotCount = configuration.getInt("anti_hostile_module_max_slot_count", "modules",
            antiHostileModuleMaxSlotCount, 1, 64,
            "Maximum number of Anti-Hostile Modules insertable across all Interdiction Matrix upgrade slots combined (1–64).");
        loadModuleConfig("anti_personnel_module",   10.0F,  "Anti-Personnel Module");
        antiPersonnelDamagePerSecond = configuration.getFloat("anti_personnel_damage_per_second", "modules",
            antiPersonnelDamagePerSecond, 0F, 10000F,
            "Damage per second dealt to non-authorized players (half-hearts). Applied each action tick proportional to actionTickRate.\n"
            + "Example: 0.5 kills an unarmoured player in ~20 s; 2.0 in ~5 s. Values >100 restore near-instakill behaviour.");
        antiPersonnelModuleMaxSlotCount = configuration.getInt("anti_personnel_module_max_slot_count", "modules",
            antiPersonnelModuleMaxSlotCount, 1, 64,
            "Maximum number of Anti-Personnel Modules insertable across all Interdiction Matrix upgrade slots combined (1–64).");
        loadModuleConfig("anti_spawn_module",      200.0F,  "Anti-Spawn Module");
        loadModuleConfig("confiscation_module",     10.0F,  "Confiscation Module");
        applyModuleCosts();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static void save() {
        if (configuration != null && configuration.hasChanged()) {
            configuration.save();
        }
    }

    private MFFSConfig() {}

    public enum InterdictionDropMode {
        NORMAL,
        COLLECTION_OPTIONAL,
        COLLECTION_REQUIRED,
        DISABLED;

        private static final String[] NAMES;

        static {
            InterdictionDropMode[] values = values();
            NAMES = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                NAMES[i] = values[i].name();
            }
        }

        public static String[] names() {
            return NAMES.clone();
        }
    }
}
