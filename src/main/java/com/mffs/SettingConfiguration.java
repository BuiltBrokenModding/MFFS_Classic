package com.mffs;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author Calclavia
 */
public class SettingConfiguration {

    public static final int MAX_FREQUENCY_DIGITS = 6;
    public static int MAX_FORCE_FIELDS_PER_TICK = 1000;
    public static int MAX_FORCE_FIELD_SCALE = 200;
    public static float FORTRON_PRODUCTION_MULTIPLIER = 1.0F;
    public static boolean INTERACT_CREATIVE = true;
    public static boolean LOAD_CHUNKS = true;
    public static boolean OP_OVERRIDE = true;
    public static boolean ENABLE_ELECTRICITY = true;
    public static boolean HIGH_GRAPHICS = true;
    public static int INTERDICTION_MURDER_ENERGY = 0;
    public static int INTERDICTION_MAX_RANGE = Integer.MAX_VALUE;
    public static boolean ENABLE_MANIPULATOR = false;
    public static short FORTRON_SYNC_TICKS = 60; // 3 seconds

    /* This is the base power required for the coercion deriver */
    public static int BASE_POWER_REQUIRED = 5_000_000;

    /* The amount of fortron required for the base capacitor to run! */
    public static int BASE_POWER_CONSUMPTION_CAPACITOR = 60;

    /* Deteremines if items will be collected upon death of personel */
    public static boolean COLLECT_ON_PERSONELL_KILL = false;

    /* Enables debug mode */
    public static boolean DEV_MODE = false;

    public static void load() {
        Configuration CONFIGURATION = ModularForcefieldSystem.modularForcefieldSystem_mod.getConfig();

        //GENERAL
        ENABLE_MANIPULATOR = CONFIGURATION.get("general", "Enable Force Manipulator", ENABLE_MANIPULATOR).getBoolean(ENABLE_MANIPULATOR);
        Property propChunkLoading = CONFIGURATION.get("general", "Load Chunks", LOAD_CHUNKS);
        propChunkLoading.comment = "Set this to false to turn off the ModularForcefieldSystem Chunkloading capabilities.";
        LOAD_CHUNKS = propChunkLoading.getBoolean(LOAD_CHUNKS);
        DEV_MODE = CONFIGURATION.getBoolean("Debug Mode", "general", DEV_MODE, "This is for developers!");

        Property propOpOverride = CONFIGURATION.get("general", "Op Override", OP_OVERRIDE);
        propOpOverride.comment = "Allow the operator(s) to override security measures created by ModularForcefieldSystem?";
        OP_OVERRIDE = propOpOverride.getBoolean(OP_OVERRIDE);

        //FORTRON
        FORTRON_PRODUCTION_MULTIPLIER = (float) CONFIGURATION.get("fortron", "Fortron Production Multiplier", FORTRON_PRODUCTION_MULTIPLIER).getDouble(FORTRON_PRODUCTION_MULTIPLIER);
        Property useElectricity = CONFIGURATION.get("fortron", "Require Electricity?", ENABLE_ELECTRICITY);
        useElectricity.comment = "Turning this to false will make ModularForcefieldSystem run without electricity or energy systems required. Great for vanilla!";
        ENABLE_ELECTRICITY = useElectricity.getBoolean(ENABLE_ELECTRICITY);
        INTERDICTION_MURDER_ENERGY = CONFIGURATION.get("fortron", "Interdiction Murder Fortron Consumption", INTERDICTION_MURDER_ENERGY).getInt(INTERDICTION_MURDER_ENERGY);
        BASE_POWER_REQUIRED = CONFIGURATION.get("fortron", "Base Coercion Deriver Power Consumption", BASE_POWER_REQUIRED).getInt();
        BASE_POWER_CONSUMPTION_CAPACITOR = CONFIGURATION.get("fortron", "Base Capacitor Power Consumption", BASE_POWER_CONSUMPTION_CAPACITOR).getInt();

        //FORCEFIELD_PROJECTOR
        Property propFieldScale = CONFIGURATION.get("forcefield_projector", "Max Force Field Scale", MAX_FORCE_FIELD_SCALE);
        MAX_FORCE_FIELD_SCALE = propFieldScale.getInt(MAX_FORCE_FIELD_SCALE);

        Property maxFFGenPerTick = CONFIGURATION.get("forcefield_projector", "Field Calculation Per Tick", MAX_FORCE_FIELDS_PER_TICK);
        maxFFGenPerTick.comment = "How many force field blocks can be generated per tick? Less reduces lag.";
        MAX_FORCE_FIELDS_PER_TICK = maxFFGenPerTick.getInt(MAX_FORCE_FIELDS_PER_TICK);

        //INTERDICTION_MATRIX
        Property interdictionRange = CONFIGURATION.get("interdiction_matrix", "Field Calculation Per Tick", INTERDICTION_MAX_RANGE);
        interdictionRange.comment = "The maximum range for the interdiction matrix.";
        INTERDICTION_MAX_RANGE = interdictionRange.getInt(INTERDICTION_MAX_RANGE);

        Property propCreative = CONFIGURATION.get("interdiction_matrix", "Effect Creative Players", INTERACT_CREATIVE);
        propCreative.comment = "Should the interdiction matrix interact with creative players?.";
        INTERACT_CREATIVE = propCreative.getBoolean(INTERACT_CREATIVE);

        Property anti_personel = CONFIGURATION.get("interdiction_matrix", "Require Collection module", COLLECT_ON_PERSONELL_KILL);
        anti_personel.comment = "Set to true for interdiction matrix to collect items from killed players without collection module.";
        COLLECT_ON_PERSONELL_KILL = anti_personel.getBoolean(COLLECT_ON_PERSONELL_KILL);

        //MODULES
        Property forceManipulatorBlacklist = CONFIGURATION.get("modules", "Force Manipulator Blacklist", "");
        forceManipulatorBlacklist.comment = "Put a list of block IDs to be not-moved by the force manipulator. Separate by commas, no space.";
        String blackListManipulate = forceManipulatorBlacklist.getString();
        //Blacklist.forceManipulationBlacklist.addAll(LanguageUtility.decodeIDSeparatedByComma(blackListManipulate));

        Property blacklist1 = CONFIGURATION.get("modules", "Stabilization Blacklist", "");
        String blackListStabilize = blacklist1.getString();
        //Blacklist.stabilizationBlacklist.addAll(LanguageUtility.decodeIDSeparatedByComma(blackListStabilize));

        Property blacklist2 = CONFIGURATION.get("modules", "Disintegration Blacklist", "");
        String blackListDisintegrate = blacklist1.getString();
        //Blacklist.disintegrationBlacklist.addAll(LanguageUtility.decodeIDSeparatedByComma(blackListDisintegrate));

        CONFIGURATION.save();

    }
}
