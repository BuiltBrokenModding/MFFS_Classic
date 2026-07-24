package dev.su5ed.mffs;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModModules;
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

    private MFFSConfig() {
    }

    public static final class Common {
        public final ModConfigSpec.BooleanValue enableElectricity;
        public final ModConfigSpec.BooleanValue useCache;
        public final ModConfigSpec.BooleanValue allowOpBiometryOverride;
        public final ModConfigSpec.BooleanValue interactCreative;
        public final ModConfigSpec.IntValue maxCustomModeScale;
        public final ModConfigSpec.BooleanValue giveGuidebookOnFirstJoin;

        public final CoercionDeriverConfig coercionDeriverConfig;
        public final FortronCapacitorConfig fortronCapacitorConfig;
        public final ProjectorConfig projectorConfig;
        public final InterdictionMatrixConfig interdictionMatrixConfig;

        public final ModConfigSpec.IntValue interdictionMatrixKillEnergy;

        public final ModConfigSpec.BooleanValue applyFieldWarpDebuff;

        private Common(ModConfigSpec.Builder builder) {
            builder.push("general");
            this.enableElectricity = builder
                .comment("Turning this to false will make MFFS run without electricity or energy systems required. Great for vanilla!")
                .define("enableElectricity", true);
            this.useCache = builder
                .comment("Cache allows temporary data saving to decrease calculations required")
                .define("useCache", true);
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

            this.coercionDeriverConfig = new CoercionDeriverConfig(builder);
            this.fortronCapacitorConfig = new FortronCapacitorConfig(builder);
            this.projectorConfig = new ProjectorConfig(builder);
            this.interdictionMatrixConfig = new InterdictionMatrixConfig(builder);

            builder.push("balance");
            this.interdictionMatrixKillEnergy = builder
                .comment("Energy to consume when the Interdiction Matrix kills a player")
                .defineInRange("interdictionMatrixKillEnergy", 0, 0, Integer.MAX_VALUE);
            builder.pop();

            builder.push("force_field");
            this.allowOpBiometryOverride = builder
                .comment("Allow server operators to bypass Force Field biometry")
                .define("allowOpBiometryOverride", true);
            this.applyFieldWarpDebuff = builder
                .comment("Apply confusion and slowness effects to players sneak-passing through force fields")
                .define("applyFieldWarpDebuff", false);
            builder.pop();
        }
    }

    public static final class ProjectorConfig {
        public final ModConfigSpec.IntValue maxFFGenPerTick;

        public final ModConfigSpec.IntValue maxFieldWidth;
        public final ModConfigSpec.IntValue maxFieldHeight;

        public final UpgradeLimitConfig upgradeLimitConfig;

        private ProjectorConfig(ModConfigSpec.Builder builder) {
            builder.push("force_field_projector");

            this.maxFFGenPerTick = builder
                .comment("How many force field blocks can be generated per tick? Less reduces lag.")
                .defineInRange("maxFFGenPerTick", 1_000_000, 0, Integer.MAX_VALUE);

            this.maxFieldWidth = builder
                .comment("Maximum horizontal size (in blocks) of a force field. 0 indicates no limit.")
                .defineInRange("maxFieldWidth", 0, 0, Integer.MAX_VALUE);
            this.maxFieldHeight = builder
                .comment("Maximum vertical size (in blocks) of a force field. 0 indicates no limit.")
                .defineInRange("maxFieldHeight", 0, 0, Integer.MAX_VALUE);

            this.upgradeLimitConfig = new UpgradeLimitConfig(builder);

            builder.pop();
        }
    }

    public static final class CoercionDeriverConfig {
        public final ModConfigSpec.IntValue fePerFortron;
        public final ModConfigSpec.IntValue fortronToFeLoss;
        public final ModConfigSpec.IntValue fortronPerTick;
        public final ModConfigSpec.IntValue fortronPerTickSpeedModule;
        public final ModConfigSpec.DoubleValue speedModuleTransferRateBonus;

        public final ModConfigSpec.DoubleValue catalystMultiplier;
        public final ModConfigSpec.IntValue catalystBurnTime;

        public final UpgradeLimitConfig upgradeLimitConfig;

        private CoercionDeriverConfig(ModConfigSpec.Builder builder) {
            builder.push("coercion_deriver");
            this.catalystMultiplier = builder
                .comment("Fortron catalyst production multiplier")
                .defineInRange("catalystMultiplier", 1.5, 0.0, 10_000.0);
            this.catalystBurnTime = builder
                .comment("The amount of ticks a single catalyst item lasts for")
                .defineInRange("catalystBurnTime", 5 * 20, 1, 10_000);

            this.fePerFortron = builder
                .comment("FE to convert into 1 Fortron")
                .defineInRange("feCostPerFortron", 600, 1, Integer.MAX_VALUE);
            this.fortronToFeLoss = builder
                .comment("FE to subtract when converting Fortron to FE")
                .defineInRange("fortronToFeLoss", 10, 0, Integer.MAX_VALUE);
            this.fortronPerTick = builder
                .comment("Base limit of fortron produced per tick (20 per second). Scales with speed modules and catalyst.")
                .defineInRange("fortronPerTick", 150, 1, Integer.MAX_VALUE);
            this.fortronPerTickSpeedModule = builder
                .comment("Production bonus per speed module. production = fortronPerTick + (fortronPerTick * speedModuleCount)... or x2 multiplicative")
                .defineInRange("fortronPerTickSpeedModule", 25, 1, Integer.MAX_VALUE);

            this.speedModuleTransferRateBonus = builder
                .comment("Increases the maximum FE transfer rate per each speed module. Each module boosts the transfer rate by DEFAULT_FE_CAPACITY * multiplier FE/t.")
                .defineInRange("speedModuleTransferRateBonus", 0.1D, 0.0D, 1.0D);

            this.upgradeLimitConfig = new UpgradeLimitConfig(builder);

            builder.pop();
        }
    }

    public static final class InterdictionMatrixConfig {
        public final ModConfigSpec.IntValue maxActionRange;
        public final ModConfigSpec.IntValue maxWarningRange;

        private InterdictionMatrixConfig(ModConfigSpec.Builder builder) {
            builder.push("interdiction_matrix");

            this.maxActionRange = builder
                .comment("Maximum action range (in blocks, as distance from interdiction matrix). 0 indicates no limit.")
                .defineInRange("maxActionRange", 0, 0, Integer.MAX_VALUE);
            this.maxWarningRange = builder
                .comment("Maximum warning range (in blocks, as distance from interdiction matrix). 0 indicates no limit.")
                .defineInRange("maxWarningRange", 0, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static final class FortronCapacitorConfig {
        public final UpgradeLimitConfig upgradeLimitConfig;

        public FortronCapacitorConfig(ModConfigSpec.Builder builder) {
            builder.comment("Fortron Capacitor").push("fortron_capacitor");

            this.upgradeLimitConfig = new UpgradeLimitConfig(builder);

            builder.pop();
        }
    }

    public static final class UpgradeLimitConfig {
        public final ModConfigSpec.IntValue maxSpeedUpgradeCount;
        public final ModConfigSpec.IntValue maxCapacityUpgradeCount;

        private UpgradeLimitConfig(ModConfigSpec.Builder builder) {
            builder.push("upgrades");

            this.maxSpeedUpgradeCount = builder
                .comment("Maximum amount of Speed Modules this device accepts. 0 indicates no limit.")
                .defineInRange("maxSpeedUpgradeCount", 0, 0, Integer.MAX_VALUE);
            this.maxCapacityUpgradeCount = builder
                .comment("Maximum amount of Capacity Modules this device accepts. 0 indicates no limit.")
                .defineInRange("maxCapacityUpgradeCount", 0, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getLimit(ModuleType<?> module) {
            if (module == ModModules.SPEED) {
                return this.maxSpeedUpgradeCount.getAsInt();
            }
            if (module == ModModules.CAPACITY) {
                return this.maxCapacityUpgradeCount.getAsInt();
            }
            return 0;
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
