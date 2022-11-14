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

        private Common(ForgeConfigSpec.Builder builder) {
            builder.push("General config");
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
            builder.pop();
        }
    }

    public static final class Client {

        private Client(ForgeConfigSpec.Builder builder) {
            
        }
    }
}
