package com.builtbroken.mffs;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Global settings for the mod
 *
 * @author Calclavia, DarkCow
 */
public class MFFSSettings
{
    //TODO move settings to JSON system, aka I want this class gone - I will do this (From Dark)

    //Projector settings
    public static int PROJECTOR_BLOCKS_PER_TICK = 1000;
    public static float PROJECTOR_UPKEEP_COST = 0.01f;
    public static int PROJECTOR_CREATION_COST = 1;
    public static boolean PROJECTOR_USE_POWER = true;

    //Interdiction matrix
    public static int INTERDICTION_ATTACK_ENERGY = 10000;
    public static int INTERDICTION_MAX_RANGE = 1000;
    public static int INTERDICTION_FORTRON_NEEDED = 30;
    public static boolean ANTI_PERSONNEL_COLLECT_ITEMS = false;

    //Network settings
    public static short FORTRON_SYNC_TICKS = 60; // 3 seconds TODO ?

    //Settings for the coercion deriver
    public static int COERCION_BATTERY_SIZE = 12000;//UE
    public static int COERCION_POWER_COST = 20; //UE, Coal generator does about 40RF/t or 80ue/t
    public static float COERCION_BATTERY_TRANSFER_PERCENTAGE = 0.05f;
    public static int COERCION_OUTPUT_PER_TICK = 40;
    public static int COERCION_FORTRON_TANK_SIZE = 30; //In buckets
    public static int COERCION_FUEL_BONUS = 4; //multiplier for output
    public static boolean COERCION_USE_POWER = true;

    //Capacitor settings
    public static int CAPACITOR_POWER_DRAIN = 0;


    protected static void loadCoercionDeriverSettings(Configuration config)
    {
        final String category = "fortron_generator";

        COERCION_POWER_COST = getIntNotNeg(config, category, "energy_cost", COERCION_POWER_COST,
                "UE energy consumed per cycle (20 cycles per second, speed modules increase cycles and thus power cost)");

        COERCION_BATTERY_SIZE = getIntNotNeg(config, category, "energy_buffer", COERCION_BATTERY_SIZE,
                "UE energy stored in the machine, make sure to scale with power usage (power = normal_power * speed(64) * bonus(4)");

        COERCION_BATTERY_TRANSFER_PERCENTAGE = config.getFloat("transfer_percentage", category, COERCION_BATTERY_TRANSFER_PERCENTAGE,
                0, 1f,
                "Percentage of power to input or output per transfer");

        COERCION_OUTPUT_PER_TICK = getIntNotNeg(config, category, "output", COERCION_OUTPUT_PER_TICK,
                "Fortron created per operation (20 operations a second)");

        COERCION_FORTRON_TANK_SIZE = getIntNotNeg(config, category, "tank_size", COERCION_FORTRON_TANK_SIZE,
                "Buckets of fortron that can be stored, scales with capacity cards");

        COERCION_FUEL_BONUS = getIntNotNeg(config, category, "fuel_bonus", COERCION_FUEL_BONUS,
                "Bonus fortron created when using fuel (output = bonus * normal_output * speed_modules)");

        COERCION_USE_POWER = config.getBoolean("use_power", category, COERCION_USE_POWER,
                "Enable to require energy to generate fortron");
    }

    protected static void loadInterdictionSettings(Configuration config)
    {
        final String category = "interdiction_matrix";
        INTERDICTION_ATTACK_ENERGY = getIntNotNeg(config, category, "attack_cost", INTERDICTION_ATTACK_ENERGY,
                "Fortron cost for attacking entities.");

        INTERDICTION_MAX_RANGE = getIntNotNeg(config, category, "range", INTERDICTION_MAX_RANGE,
                "The maximum range for the interdiction matrix.");

        ANTI_PERSONNEL_COLLECT_ITEMS = config.getBoolean("collect_items", category, ANTI_PERSONNEL_COLLECT_ITEMS,
                "Set to true for interdiction matrix to collect items from killed players without collection module.");

        INTERDICTION_FORTRON_NEEDED = getIntNotNeg(config, category, "fortron_amount", INTERDICTION_FORTRON_NEEDED,
                "Upkeep cost for the running of the Interdiction Matrix");
    }

    protected static void loadProjectorSettings(Configuration config)
    {
        final String category = "forcefield_projector";
        PROJECTOR_BLOCKS_PER_TICK = getIntNotNeg(config, category, "blocks_per_tick", PROJECTOR_BLOCKS_PER_TICK,
                "How many blocks can be generated per tick. Decreasing this can improve TPS, at the cost of response time of fields");
    }

    protected static void loadCapacitorSettings(Configuration config)
    {
        final String category = "capacitor";
        CAPACITOR_POWER_DRAIN = getIntNotNeg(config, category, "fortron_cost", CAPACITOR_POWER_DRAIN,
                "Upkeep cost for running the capacitor.");
    }

    protected static void loadRenderSettings(Configuration config)
    {
        final String category = "rendering";
        //TODO implement settings to disable beam render
    }

    /**
     * Called to load the settings
     *
     * @param config
     */
    public static void load(Configuration config)
    {
        config.load();
        loadCoercionDeriverSettings(config);
        loadInterdictionSettings(config);
        loadProjectorSettings(config);
        loadCapacitorSettings(config);
        loadRenderSettings(config);
        config.save();
    }

    /**
     * Gets an int with a value between zero and {@link Integer#MAX_VALUE}
     *
     * @param config
     * @param name
     * @param category
     * @param defaultValue
     * @param comment
     * @return
     */
    public static int getIntNotNeg(Configuration config, String category, String name, int defaultValue, String comment)
    {
        Property prop = config.get(category, name, defaultValue);
        prop.comment = comment + " [range: " + 0 + " ~ Integer.MAX, default: " + defaultValue + "]";
        return prop.getInt(defaultValue) < 0 ? 0 : prop.getInt(defaultValue);
    }

}
