package dev.su5ed.mffs;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MFFSConfig {
    static final ForgeConfigSpec COMMON_SPEC;
    static final ForgeConfigSpec CLIENT_SPEC;

    public static final Common COMMON;
    public static final Client CLIENT;

    static {
        Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();

        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    private MFFSConfig() {}

    public static final class Common {
        public final ForgeConfigSpec.BooleanValue enableElectricity;
        public final ForgeConfigSpec.BooleanValue useCache;
        public final ForgeConfigSpec.IntValue maxFFGenPerTick;
        public final ForgeConfigSpec.BooleanValue allowOpBiometryOverride;
        public final ForgeConfigSpec.BooleanValue interactCreative;
        public final ForgeConfigSpec.IntValue maxCustomModeScale;
        public final ForgeConfigSpec.BooleanValue giveGuidebookOnFirstJoin;

        public final ForgeConfigSpec.IntValue coercionDriverFePerFortron;
        public final ForgeConfigSpec.IntValue coercionDriverFortronToFeLoss;
        public final ForgeConfigSpec.IntValue coercionDriverFortronPerTick;
        public final ForgeConfigSpec.IntValue coercionDriverFortronPerTickSpeedModule;

        public final ForgeConfigSpec.DoubleValue catalystMultiplier;
        public final ForgeConfigSpec.IntValue catalystBurnTime;

        public final ForgeConfigSpec.IntValue interdictionMatrixKillEnergy;

        private Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            this.enableElectricity = builder
                .comment("Turning this to false will make MFFS run without electricity or energy systems required. Great for vanilla!")
                .define("enableElectricity", true);
            this.useCache = builder
                .comment("Cache allows temporary data saving to decrease calculations required")
                .define("useCache", true);
            this.maxFFGenPerTick = builder
                .comment("How many force field blocks can be generated per tick? Less reduces lag.")
                .defineInRange("maxFFGenPerTick", 1_000_000, 0, Integer.MAX_VALUE);
            this.allowOpBiometryOverride = builder
                .comment("Allow server operators to bypass Force Field biometry")
                .define("allowOpBiometryOverride", true);
            this.interactCreative = builder
                .comment("Should the interdiction matrix interact with creative players?")
                .define("interactCreative", true);
            this.maxCustomModeScale = builder
                .comment("Max custom mode field scale")
                .defineInRange("maxCustomModeScale", 200, 0, Integer.MAX_VALUE);
            this.giveGuidebookOnFirstJoin = builder
                .comment("Give players a copy of the MFFS guidebook when they first join a world")
                .define("giveGuidebookOnFirstJoin", true);
            builder.pop();

            builder.push("coercion_deriver");
            this.coercionDriverFePerFortron = builder
                    .comment("FE to convert into 1 Fortron")
                    .defineInRange("feCostPerFortron", 400, 1, Integer.MAX_VALUE);
            this.coercionDriverFortronToFeLoss = builder
                    .comment("FE to subtract when converting Fortron to FE")
                    .defineInRange("fortronToFeLoss", 1, 0, Integer.MAX_VALUE);
            this.coercionDriverFortronPerTick = builder
                    .comment("Base limit of fortron produced per tick (20 per second). Scales with speed modules and catalyst.")
                    .defineInRange("fortronPerTick", 200, 1, Integer.MAX_VALUE);
            coercionDriverFortronPerTickSpeedModule = builder
                    .comment("Production bonus per speed module. production = fortronPerTick + (fortronPerTick * speedModuleCount)... or x2 multiplicative")
                    .defineInRange("fortronPerTickSpeedModule", 200, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("balance");
            this.catalystMultiplier = builder
                .comment("Fortron catalyst production multiplier")
                .defineInRange("catalystMultiplier", 2.0, 0.0, 10_000.0);
            this.catalystBurnTime = builder
                .comment("The amount of ticks a single catalyst item lasts for")
                .defineInRange("catalystBurnTime", 10 * 20, 1, 10_000);
            this.interdictionMatrixKillEnergy = builder
                .comment("Energy to consume when the Interdiction Matrix kills a player")
                .defineInRange("interdictionMatrixKillEnergy", 0, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static final class Client {
        public final ForgeConfigSpec.BooleanValue enableProjectorModeGlitch;

        private Client(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            this.enableProjectorModeGlitch = builder
                .comment("Apply a fancy glitch effect on projector mode renders. Reload resources to apply change.")
                .define("enableProjectorModeGlitch", true);
            builder.pop();
        }
    }
}
