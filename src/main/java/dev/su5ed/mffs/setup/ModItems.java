package dev.su5ed.mffs.setup;

// =============================================================================
// 1.12.2 Backport: Item registration
// =============================================================================

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.item.*;
import dev.su5ed.mffs.util.projector.ModProjectorModes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = MFFSMod.MODID)
public final class ModItems {

    // Creative tab - all MFFS items appear here
    public static final CreativeTabs ITEM_GROUP = new CreativeTabs(MFFSMod.MODID + ".main") {
        @Override
        public ItemStack createIcon() {
            return PROJECTOR_ITEM != null ? new ItemStack(PROJECTOR_ITEM) : ItemStack.EMPTY;
        }
    };

    // Static item references - populated during RegistryEvent.Register<Item>
    public static Item PROJECTOR_ITEM;
    public static Item COERCION_DERIVER_ITEM;
    public static Item FORTRON_CAPACITOR_ITEM;
    public static Item BIOMETRIC_IDENTIFIER_ITEM;
    public static Item INTERDICTION_MATRIX_ITEM;
    public static RemoteControllerItem REMOTE_CONTROLLER_ITEM;
    public static Item FOCUS_MATRIX;
    public static ProjectorModeItem CUBE_MODE;
    public static ProjectorModeItem SPHERE_MODE;
    public static ProjectorModeItem TUBE_MODE;
    public static ProjectorModeItem PYRAMID_MODE;
    public static ProjectorModeItem CYLINDER_MODE;
    public static CustomProjectorModeItem CUSTOM_MODE;
    public static ModuleItem<Module> TRANSLATION_MODULE;
    public static ModuleItem<Module> SCALE_MODULE;
    public static ModuleItem<Module> ROTATION_MODULE;
    public static ModuleItem<Module> SPEED_MODULE;
    public static ModuleItem<Module> CAPACITY_MODULE;
    public static ModuleItem<Module> FUSION_MODULE;
    public static ModuleItem<Module> DOME_MODULE;
    public static ModuleItem<Module> CAMOUFLAGE_MODULE;
    public static ModuleItem<Module> DISINTEGRATION_MODULE;
    public static ModuleItem<Module> SHOCK_MODULE;
    public static ModuleItem<Module> GLOW_MODULE;
    public static ModuleItem<Module> SPONGE_MODULE;
    public static ModuleItem<Module> STABILIZATION_MODULE;
    public static ModuleItem<Module> COLLECTION_MODULE;
    public static ModuleItem<Module> INVERTER_MODULE;
    public static ModuleItem<Module> SILENCE_MODULE;
    public static InterdictionMatrixModuleItem WARN_MODULE;
    public static InterdictionMatrixModuleItem BLOCK_ACCESS_MODULE;
    public static InterdictionMatrixModuleItem BLOCK_ALTER_MODULE;
    public static InterdictionMatrixModuleItem ANTI_FRIENDLY_MODULE;
    public static InterdictionMatrixModuleItem ANTI_HOSTILE_MODULE;
    public static InterdictionMatrixModuleItem ANTI_PERSONNEL_MODULE;
    public static InterdictionMatrixModuleItem ANTI_SPAWN_MODULE;
    public static InterdictionMatrixModuleItem CONFISCATION_MODULE;
    public static Item BLANK_CARD;
    public static IdentificationCardItem ID_CARD;
    public static BaseItem INFINITE_POWER_CARD;
    public static FrequencyCardItem FREQUENCY_CARD;
    public static Item STEEL_COMPOUND;
    public static Item STEEL_INGOT;
    public static BatteryItem BATTERY;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Block items (ItemBlock wrappers) — no ItemBlock for FORCE_FIELD (not player-obtainable)
        PROJECTOR_ITEM            = register(registry, new ItemBlock(ModBlocks.PROJECTOR), "projector");
        COERCION_DERIVER_ITEM     = register(registry, new ItemBlock(ModBlocks.COERCION_DERIVER), "coercion_deriver");
        FORTRON_CAPACITOR_ITEM    = register(registry, new ItemBlock(ModBlocks.FORTRON_CAPACITOR), "fortron_capacitor");
        BIOMETRIC_IDENTIFIER_ITEM = register(registry, new ItemBlock(ModBlocks.BIOMETRIC_IDENTIFIER), "biometric_identifier");
        INTERDICTION_MATRIX_ITEM  = register(registry, new ItemBlock(ModBlocks.INTERDICTION_MATRIX), "interdiction_matrix");

        // Tools / cards
        REMOTE_CONTROLLER_ITEM = register(registry, new RemoteControllerItem(), "remote_controller");
        FREQUENCY_CARD         = register(registry, new FrequencyCardItem(), "frequency_card");
        ID_CARD                = register(registry, new IdentificationCardItem(), "id_card");
        BLANK_CARD             = register(registry, new Item(), "blank_card");
        INFINITE_POWER_CARD    = register(registry, singleItem(true), "infinite_power_card");
        FOCUS_MATRIX           = register(registry, new Item(), "focus_matrix");
        
        // Steel items — disabled by default as other mods provide them
        if (!MFFSConfig.disableSteelItems) {
            STEEL_COMPOUND         = register(registry, new Item(), "steel_compound");
            STEEL_INGOT            = register(registry, new Item(), "steel_ingot");
        }
        
        BATTERY                = register(registry, new BatteryItem(), "battery");

        // Projector modes
        CUBE_MODE     = register(registry, new ProjectorModeItem(ModProjectorModes.CUBE), "cube_mode");
        SPHERE_MODE   = register(registry, new ProjectorModeItem(ModProjectorModes.SPHERE), "sphere_mode");
        TUBE_MODE     = register(registry, new ProjectorModeItem(ModProjectorModes.TUBE), "tube_mode");
        PYRAMID_MODE  = register(registry, new ProjectorModeItem(ModProjectorModes.PYRAMID), "pyramid_mode");
        CYLINDER_MODE = register(registry, new ProjectorModeItem(ModProjectorModes.CYLINDER), "cylinder_mode");
        CUSTOM_MODE   = register(registry, new CustomProjectorModeItem(), "custom_mode");

        // Field / general modules (gated by per-module config)
        if (MFFSConfig.isModuleEnabled("translation_module"))
            TRANSLATION_MODULE = register(registry, new ModuleItem<>(ModModules.TRANSLATION), "translation_module");
        if (MFFSConfig.isModuleEnabled("scale_module"))
            SCALE_MODULE = register(registry, new ModuleItem<>(ModModules.SCALE), "scale_module");
        if (MFFSConfig.isModuleEnabled("rotation_module"))
            ROTATION_MODULE = register(registry, new ModuleItem<>(ModModules.ROTATION), "rotation_module");
        if (MFFSConfig.isModuleEnabled("speed_module"))
            SPEED_MODULE = register(registry, new ModuleItem<>(ModModules.SPEED), "speed_module");
        if (MFFSConfig.isModuleEnabled("capacity_module"))
            CAPACITY_MODULE = register(registry, new ModuleItem<>(ModModules.CAPACITY), "capacity_module");
        if (MFFSConfig.isModuleEnabled("fusion_module"))
            FUSION_MODULE = register(registry, singleModule(ModModules.FUSION), "fusion_module");
        if (MFFSConfig.isModuleEnabled("dome_module"))
            DOME_MODULE = register(registry, singleModule(ModModules.DOME), "dome_module");
        if (MFFSConfig.isModuleEnabled("camouflage_module"))
            CAMOUFLAGE_MODULE = register(registry, singleModule(ModModules.CAMOUFLAGE), "camouflage_module");
        if (MFFSConfig.isModuleEnabled("disintegration_module"))
            DISINTEGRATION_MODULE = register(registry, singleModule(ModModules.DISINTEGRATION), "disintegration_module");
        if (MFFSConfig.isModuleEnabled("shock_module"))
            SHOCK_MODULE = register(registry, new ModuleItem<>(ModModules.SHOCK), "shock_module");
        if (MFFSConfig.isModuleEnabled("glow_module"))
            GLOW_MODULE = register(registry, new ModuleItem<>(ModModules.GLOW), "glow_module");
        if (MFFSConfig.isModuleEnabled("sponge_module"))
            SPONGE_MODULE = register(registry, singleModule(ModModules.SPONGE), "sponge_module");
        if (MFFSConfig.isModuleEnabled("stabilization_module"))
            STABILIZATION_MODULE = register(registry, singleModule(ModModules.STABILIZAZION), "stabilization_module");
        if (MFFSConfig.isModuleEnabled("collection_module"))
            COLLECTION_MODULE = register(registry, singleModule(ModModules.COLLECTION), "collection_module");
        if (MFFSConfig.isModuleEnabled("inverter_module"))
            INVERTER_MODULE = register(registry, singleModule(ModModules.INVERTER), "inverter_module");
        if (MFFSConfig.isModuleEnabled("silence_module"))
            SILENCE_MODULE = register(registry, singleModule(ModModules.SILENCE), "silence_module");

        // Interdiction matrix modules (gated by per-module config)
        if (MFFSConfig.isModuleEnabled("warn_module"))
            WARN_MODULE = register(registry, new InterdictionMatrixModuleItem(ModModules.WARN), "warn_module");
        if (MFFSConfig.isModuleEnabled("block_access_module"))
            BLOCK_ACCESS_MODULE = register(registry, singleIMModule(ModModules.BLOCK_ACCESS), "block_access_module");
        if (MFFSConfig.isModuleEnabled("block_alter_module"))
            BLOCK_ALTER_MODULE = register(registry, singleIMModule(ModModules.BLOCK_ALTER), "block_alter_module");
        if (MFFSConfig.isModuleEnabled("anti_friendly_module"))
            ANTI_FRIENDLY_MODULE = register(registry, new InterdictionMatrixModuleItem(ModModules.ANTI_FRIENDLY), "anti_friendly_module");
        if (MFFSConfig.isModuleEnabled("anti_hostile_module"))
            ANTI_HOSTILE_MODULE = register(registry, new InterdictionMatrixModuleItem(ModModules.ANTI_HOSTILE), "anti_hostile_module");
        if (MFFSConfig.isModuleEnabled("anti_personnel_module"))
            ANTI_PERSONNEL_MODULE = register(registry, new InterdictionMatrixModuleItem(ModModules.ANTI_PERSONNEL), "anti_personnel_module");
        if (MFFSConfig.isModuleEnabled("anti_spawn_module"))
            ANTI_SPAWN_MODULE = register(registry, singleIMModule(ModModules.ANTI_SPAWN), "anti_spawn_module");
        if (MFFSConfig.isModuleEnabled("confiscation_module"))
            CONFISCATION_MODULE = register(registry, singleIMModule(ModModules.CONFISCATION), "confiscation_module");
    }

    /** Creates an InterdictionMatrixModuleItem with maxStackSize=1. */
    private static InterdictionMatrixModuleItem singleIMModule(ModuleType<InterdictionMatrixModule> type) {
        InterdictionMatrixModuleItem item = new InterdictionMatrixModuleItem(type);
        item.setMaxStackSize(1);
        return item;
    }

    /** Creates a ModuleItem with maxStackSize=1 (for modules that can only be used once). */
    private static ModuleItem<Module> singleModule(ModuleType<Module> type) {
        ModuleItem<Module> item = new ModuleItem<>(type);
        item.setMaxStackSize(1);
        return item;
    }

    /** Creates a BaseItem with maxStackSize=1 (for single-use cards). */
    private static BaseItem singleItem(boolean showDescription) {
        BaseItem item = new BaseItem(showDescription);
        item.setMaxStackSize(1);
        return item;
    }

    private static <T extends Item> T register(IForgeRegistry<Item> registry, T item, String name) {
        item.setRegistryName(MFFSMod.MODID, name);
        item.setTranslationKey("mffs." + name);
        item.setCreativeTab(ITEM_GROUP);
        registry.register(item);
        return item;
    }

    private ModItems() {}
}
