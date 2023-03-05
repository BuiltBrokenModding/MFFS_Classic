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
        public final ForgeConfigSpec.DoubleValue fortronProductionMultiplier;
        public final ForgeConfigSpec.BooleanValue useCache;
        public final ForgeConfigSpec.IntValue maxFFGenPerTick;
        public final ForgeConfigSpec.BooleanValue allowOpBiometryOverride;

        public final ForgeConfigSpec.DoubleValue energyConversionRatio;
        public final ForgeConfigSpec.DoubleValue catalystMultiplier;
        public final ForgeConfigSpec.IntValue catalystBurnTime;
        public final ForgeConfigSpec.DoubleValue backConversionEnergyLoss;

        private Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            this.enableElectricity = builder
                .comment("Turning this to false will make MFFS run without electricity or energy systems required. Great for vanilla!")
                .define("enableElectricity", true);
            this.fortronProductionMultiplier = builder
                .comment("Fortron Production Multiplier")
                .defineInRange("fortronProductionMultiplier", 1.0, 0, 10);
            this.useCache = builder
                .comment("Cache allows temporary data saving to decrease calculations required")
                .define("useCache", true);
            this.maxFFGenPerTick = builder
                .comment("How many force field blocks can be generated per tick? Less reduces lag.")
                .defineInRange("maxFFGenPerTick", 1000000, 0, Integer.MAX_VALUE);
            this.allowOpBiometryOverride = builder
                .comment("Allow server operators to bypass Force Field biometry")
                .define("allowOpBiometryOverride", true);
            builder.pop();

            builder.push("balance");
            this.energyConversionRatio = builder
                .comment("The amount of generated Fortron per 1 FE")
                .defineInRange("energyConversionRatio", 0.0025, 0.00001, 10000);
            this.catalystMultiplier = builder
                .comment("Fortron catalyst production multiplier")
                .defineInRange("catalystMultiplier", 4.0, 0.0, 10000.0);
            this.catalystBurnTime = builder
                .comment("The amount of ticks a single catalyst item lasts for")
                .defineInRange("catalystBurnTime", 10 * 20, 1, 10000);
            this.backConversionEnergyLoss = builder
                .comment("Energy loss per tick when converting Fortron back to FE")
                .defineInRange("backConversionEnergyLoss", 1.0, 0.0, 10000.0);
            builder.pop();
        }
    }

    public static final class Client {

        private Client(ForgeConfigSpec.Builder builder) {

        }
    }
}
