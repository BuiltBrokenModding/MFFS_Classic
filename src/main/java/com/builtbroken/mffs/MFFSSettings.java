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
    //TODO clean up this F###ing mess
    //TODO move settings to content/components they are used by
    //TODO move settings to JSON system, aka I want this class gone

    //Projector settings
    public static int PROJECTOR_BLOCKS_PER_TICK = 1000;
    public static float PROJECTOR_COST_PER_FIELD = 0.01f;
    public static int PROJECTOR_COST_PER_FIELD_CREATION = 1;

    //Interdiction matrix
    public static int INTERDICTION_ATTACK_ENERGY = 10000;
    public static int INTERDICTION_MAX_RANGE = 1000;
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

        COERCION_POWER_COST = config.getInt(category, "energy_cost", COERCION_POWER_COST, 0, Integer.MAX_VALUE,
                "UE energy consumed per cycle (20 cycles per second, speed modules increase cycles and thus power cost)");

        COERCION_BATTERY_SIZE = config.getInt(category, "energy_buffer", COERCION_BATTERY_SIZE, 0, Integer.MAX_VALUE,
                "UE energy stored in the machine, make sure to scale with power usage (power = normal_power * speed(64) * bonus(4)");

        COERCION_BATTERY_TRANSFER_PERCENTAGE = config.getFloat(category, "transfer_percentage", COERCION_BATTERY_TRANSFER_PERCENTAGE, 0, 1f,
                "Percentage of power to input or output per transfer");

        COERCION_OUTPUT_PER_TICK = config.getInt(category, "output", COERCION_OUTPUT_PER_TICK, 0, Integer.MAX_VALUE,
                "Fortron created per operation (20 operations a second)");

        COERCION_FORTRON_TANK_SIZE = config.getInt(category, "tank_size", COERCION_FORTRON_TANK_SIZE, 0, Short.MAX_VALUE,
                "Buckets of fortron that can be stored, scales with capacity cards");

        COERCION_FUEL_BONUS = config.getInt(category, "fuel_bonus", COERCION_FUEL_BONUS, 0, Short.MAX_VALUE,
                "Bonus fortron created when using fuel (output = bonus * normal_output * speed_modules)");

        COERCION_USE_POWER = config.getBoolean("use_power", category, COERCION_USE_POWER,
                "Enable to require energy to generate fortron");
    }

    protected static void loadInterdictionSettings(Configuration config)
    {
        final String category = "interdiction_matrix";
        INTERDICTION_ATTACK_ENERGY = config.getInt(category, "attack_cost", INTERDICTION_ATTACK_ENERGY, 0, 100000,
                "Fortron cost for attacking entities.");

        Property interdictionRange = config.get(category, "range", INTERDICTION_MAX_RANGE);
        interdictionRange.comment = "The maximum range for the interdiction matrix.";
        INTERDICTION_MAX_RANGE = interdictionRange.getInt(INTERDICTION_MAX_RANGE);

        Property anti_personel = config.get(category, "collect_items", ANTI_PERSONNEL_COLLECT_ITEMS);
        anti_personel.comment = "Set to true for interdiction matrix to collect items from killed players without collection module.";
        ANTI_PERSONNEL_COLLECT_ITEMS = anti_personel.getBoolean(ANTI_PERSONNEL_COLLECT_ITEMS);
    }

    protected static void loadProjectorSettings(Configuration config)
    {
        Property maxFFGenPerTick = config.get("forcefield_projector", "blocks_per_tick", PROJECTOR_BLOCKS_PER_TICK);
        maxFFGenPerTick.comment = "How many blocks can be generated per tick. Decreasing this can improve TPS, at the cost of response time of fields";
        PROJECTOR_BLOCKS_PER_TICK = maxFFGenPerTick.getInt(PROJECTOR_BLOCKS_PER_TICK);
    }

    protected static void loadCapacitorSettings(Configuration config)
    {
        CAPACITOR_POWER_DRAIN = config.getInt("capacitor", "fortron_cost", CAPACITOR_POWER_DRAIN, 0, 10000, "Upkeep cost for running the capacitor.");
    }

    protected static void loadRenderSettings(Configuration config)
    {
        final String category = "rendering";
        //TODO implement settings to disable beam render
    }

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
}
