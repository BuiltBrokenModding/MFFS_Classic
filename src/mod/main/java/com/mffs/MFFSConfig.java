package com.mffs;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

/**
 * Created by pwaln on 6/15/2016.
 */
public class MFFSConfig {

    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Modular Force Field System.cfg"));
    public static final int MAX_FREQUENCY_DIGITS = 6;
    public static int MAX_FORCE_FIELDS_PER_TICK = 1000;
    public static int MAX_FORCE_FIELD_SCALE = 200;
    public static float FORTRON_PRODUCTION_MULTIPLIER = 1.0F;
    public static boolean INTERACT_CREATIVE = true;
    public static boolean LOAD_CHUNKS = true;
    public static boolean OP_OVERRIDE = true;
    public static boolean ENABLE_ELECTRICITY = true;
    public static boolean CONSERVE_PACKETS = true;
    public static boolean HIGH_GRAPHICS = true;
    public static int INTERDICTION_MURDER_ENERGY = 0;
    public static int INTERDICTION_MAX_RANGE = Integer.MAX_VALUE;
    public static boolean ENABLE_MANIPULATOR = true;
    public static short FORTRON_SYNC_TICKS = 60; // 3 seconds

    /*     */
/*     */
    public static void load()
/*     */ {
        CONFIGURATION.load();
/*     */
        ENABLE_MANIPULATOR = CONFIGURATION.get("general", "Enable Force Manipulator", ENABLE_MANIPULATOR).getBoolean(ENABLE_MANIPULATOR);
/*     */
        FORTRON_PRODUCTION_MULTIPLIER = (float) CONFIGURATION.get("general", "Fortron Production Multiplier", FORTRON_PRODUCTION_MULTIPLIER).getDouble(FORTRON_PRODUCTION_MULTIPLIER);
/*     */
        Property propFieldScale = CONFIGURATION.get("general", "Max Force Field Scale", MAX_FORCE_FIELD_SCALE);
        MAX_FORCE_FIELD_SCALE = propFieldScale.getInt(MAX_FORCE_FIELD_SCALE);
/*     */
        Property propInterdiction = CONFIGURATION.get("general", "Interdiction Murder Fortron Consumption", INTERDICTION_MURDER_ENERGY);
        INTERDICTION_MURDER_ENERGY = propInterdiction.getInt(INTERDICTION_MURDER_ENERGY);
/*     */
        Property propCreative = CONFIGURATION.get("general", "Effect Creative Players", INTERACT_CREATIVE);
        propCreative.comment = "Should the interdiction matrix interact with creative players?.";
        INTERACT_CREATIVE = propCreative.getBoolean(INTERACT_CREATIVE);
/*     */
        Property propChunkLoading = CONFIGURATION.get("general", "Load Chunks", LOAD_CHUNKS);
        propChunkLoading.comment = "Set this to false to turn off the MFFS Chunkloading capabilities.";
        LOAD_CHUNKS = propChunkLoading.getBoolean(LOAD_CHUNKS);
/*     */
        Property propOpOverride = CONFIGURATION.get("general", "Op Override", OP_OVERRIDE);
        propOpOverride.comment = "Allow the operator(s) to override security measures created by MFFS?";
        OP_OVERRIDE = propOpOverride.getBoolean(OP_OVERRIDE);
/*     */
/*     */
        Property maxFFGenPerTick = CONFIGURATION.get("general", "Field Calculation Per Tick", MAX_FORCE_FIELDS_PER_TICK);
        maxFFGenPerTick.comment = "How many force field blocks can be generated per tick? Less reduces lag.";
        MAX_FORCE_FIELDS_PER_TICK = maxFFGenPerTick.getInt(MAX_FORCE_FIELDS_PER_TICK);
/*     */
        Property interdictionRange = CONFIGURATION.get("general", "Field Calculation Per Tick", INTERDICTION_MAX_RANGE);
        interdictionRange.comment = "The maximum range for the interdiction matrix.";
        INTERDICTION_MAX_RANGE = interdictionRange.getInt(INTERDICTION_MAX_RANGE);
/*     */
        Property useElectricity = CONFIGURATION.get("general", "Require Electricity?", ENABLE_ELECTRICITY);
        useElectricity.comment = "Turning this to false will make MFFS run without electricity or energy systems required. Great for vanilla!";
        ENABLE_ELECTRICITY = useElectricity.getBoolean(ENABLE_ELECTRICITY);
/*     */
        Property conservePackets = CONFIGURATION.get("general", "Conserve Packets?", CONSERVE_PACKETS);
        conservePackets.comment = "Turning this to false will enable better client side packet and updates but in the cost of more packets sent.";
        CONSERVE_PACKETS = conservePackets.getBoolean(CONSERVE_PACKETS);
/*     */
        Property highGraphics = CONFIGURATION.get("general", "High Graphics", HIGH_GRAPHICS);
        highGraphics.comment = "Turning this to false will reduce rendering and client side packet graphical packets.";
        CONSERVE_PACKETS = highGraphics.getBoolean(HIGH_GRAPHICS);
/*     */
        Property forceManipulatorBlacklist = CONFIGURATION.get("general", "Force Manipulator Blacklist", "");
        forceManipulatorBlacklist.comment = "Put a list of block IDs to be not-moved by the force manipulator. Separate by commas, no space.";
        String blackListManipulate = forceManipulatorBlacklist.getString();
        //Blacklist.forceManipulationBlacklist.addAll(LanguageUtility.decodeIDSeparatedByComma(blackListManipulate));
/*     */
        Property blacklist1 = CONFIGURATION.get("general", "Stabilization Blacklist", "");
        String blackListStabilize = blacklist1.getString();
        //Blacklist.stabilizationBlacklist.addAll(LanguageUtility.decodeIDSeparatedByComma(blackListStabilize));
/*     */
        Property blacklist2 = CONFIGURATION.get("general", "Disintegration Blacklist", "");
        String blackListDisintegrate = blacklist1.getString();
        //Blacklist.disintegrationBlacklist.addAll(LanguageUtility.decodeIDSeparatedByComma(blackListDisintegrate));
/*     */
        CONFIGURATION.save();
/*     */
    }
}
