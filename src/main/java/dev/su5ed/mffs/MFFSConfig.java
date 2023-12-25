package dev.su5ed.mffs;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MFFSConfig {
    static final ModConfigSpec COMMON_SPEC;
    static final ModConfigSpec CLIENT_SPEC;

    public static final Common COMMON;
    public static final Client CLIENT;

    static {
        Pair<Common, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();

        Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    private MFFSConfig() {}

    public static final class Common {
        public final ModConfigSpec.BooleanValue enableElectricity;
        public final ModConfigSpec.BooleanValue useCache;
        public final ModConfigSpec.IntValue maxFFGenPerTick;
        public final ModConfigSpec.BooleanValue allowOpBiometryOverride;
        public final ModConfigSpec.BooleanValue interactCreative;
        public final ModConfigSpec.IntValue maxCustomModeScale;
        public final ModConfigSpec.BooleanValue giveGuidebookOnFirstJoin;

        public final ModConfigSpec.DoubleValue energyConversionRatio;
        public final ModConfigSpec.DoubleValue catalystMultiplier;
        public final ModConfigSpec.IntValue catalystBurnTime;
        public final ModConfigSpec.DoubleValue backConversionEnergyLoss;
        public final ModConfigSpec.IntValue interdictionMatrixKillEnergy;

        private Common(ModConfigSpec.Builder builder) {
            builder.push("general");
            this.enableElectricity = builder
                .comment("Turning this to false will make MFFS run without electricity or energy systems required. Great for vanilla!")
                .define("enableElectricity", true);
            this.useCache = builder
                .comment("Cache allows temporary data saving to decrease calculations required")
                .define("useCache", true);
            this.maxFFGenPerTick = builder
                .comment("How many force field blocks can be generated per tick? Less reduces lag.")
                .defineInRange("maxFFGenPerTick", 1000000, 0, Integer.MAX_VALUE);
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

            builder.push("balance");
            this.energyConversionRatio = builder
                .comment("The amount of generated Fortron per 1 FE")
                .defineInRange("energyConversionRatio", 0.0025, 0.00001, 10000);
            this.catalystMultiplier = builder
                .comment("Fortron catalyst production multiplier")
                .defineInRange("catalystMultiplier", 2.0, 0.0, 10000.0);
            this.catalystBurnTime = builder
                .comment("The amount of ticks a single catalyst item lasts for")
                .defineInRange("catalystBurnTime", 10 * 20, 1, 10000);
            this.backConversionEnergyLoss = builder
                .comment("Energy loss per tick when converting Fortron back to FE")
                .defineInRange("backConversionEnergyLoss", 1.0, 0.0, 10000.0);
            this.interdictionMatrixKillEnergy = builder
                .comment("Energy to consume when the Interdiction Matrix kills a player")
                .defineInRange("interdictionMatrixKillEnergy", 0, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static final class Client {
        public final ModConfigSpec.BooleanValue enableProjectorModeGlitch;

        private Client(ModConfigSpec.Builder builder) {
            builder.push("general");
            this.enableProjectorModeGlitch = builder
                .comment("Apply a fancy glitch effect on projector mode renders. Reload resources to apply change.")
                .define("enableProjectorModeGlitch", true);
            builder.pop();
        }
    }
}
