package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.item.*;
import dev.su5ed.mffs.item.BaseItem.ExtendedItemProperties;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.projector.ModProjectorModes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MFFSMod.MODID);
    private static final DeferredRegister<CreativeModeTab> ITEM_GROUPS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MFFSMod.MODID);

    public static final DeferredItem<Item> PROJECTOR_ITEM = fromBlock(ModBlocks.PROJECTOR);
    public static final DeferredItem<Item> COERCION_DERIVER_ITEM = fromBlock(ModBlocks.COERCION_DERIVER);
    public static final DeferredItem<Item> FORTRON_CAPACITOR_ITEM = fromBlock(ModBlocks.FORTRON_CAPACITOR);
    public static final DeferredItem<Item> BIOMETRIC_IDENTIFIER_ITEM = fromBlock(ModBlocks.BIOMETRIC_IDENTIFIER);
    public static final DeferredItem<Item> INTERDICTION_MATRIX_ITEM = fromBlock(ModBlocks.INTERDICTION_MATRIX);
    public static final DeferredItem<Item> REMOTE_CONTROLLER_ITEM = ITEMS.registerItem("remote_controller", RemoteControllerItem::new);

    public static final DeferredItem<Item> FOCUS_MATRIX = ITEMS.registerItem("focus_matrix", Item::new);
    public static final DeferredItem<ProjectorModeItem> CUBE_MODE = projectorMode("cube_mode", ModProjectorModes.CUBE);
    public static final DeferredItem<ProjectorModeItem> SPHERE_MODE = projectorMode("sphere_mode", ModProjectorModes.SPHERE);
    public static final DeferredItem<ProjectorModeItem> TUBE_MODE = projectorMode("tube_mode", ModProjectorModes.TUBE);
    public static final DeferredItem<ProjectorModeItem> PYRAMID_MODE = projectorMode("pyramid_mode", ModProjectorModes.PYRAMID);
    public static final DeferredItem<ProjectorModeItem> CYLINDER_MODE = projectorMode("cylinder_mode", ModProjectorModes.CYLINDER);
    public static final DeferredItem<CustomProjectorModeItem> CUSTOM_MODE = ITEMS.registerItem("custom_mode", CustomProjectorModeItem::new);
    public static final DeferredItem<ModuleItem<Module>> TRANSLATION_MODULE = module("translation_module", ModModules.TRANSLATION, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> SCALE_MODULE = module("scale_module", ModModules.SCALE, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> ROTATION_MODULE = module("rotation_module", ModModules.ROTATION, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> SPEED_MODULE = module("speed_module", ModModules.SPEED, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> CAPACITY_MODULE = module("capacity_module", ModModules.CAPACITY, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> FUSION_MODULE = module("fusion_module", ModModules.FUSION, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<Module>> DOME_MODULE = module("dome_module", ModModules.DOME, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> CAMOUFLAGE_MODULE = module("camouflage_module", ModModules.CAMOUFLAGE, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<Module>> DISINTEGRATION_MODULE = module("disintegration_module", ModModules.DISINTEGRATION, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<Module>> SHOCK_MODULE = module("shock_module", ModModules.SHOCK, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> GLOW_MODULE = module("glow_module", ModModules.GLOW, ExtendedItemProperties::description);
    public static final DeferredItem<ModuleItem<Module>> SPONGE_MODULE = module("sponge_module", ModModules.SPONGE, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<Module>> STABILIZATION_MODULE = module("stabilization_module", ModModules.STABILIZAZION, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<Module>> COLLECTION_MODULE = module("collection_module", ModModules.COLLECTION, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<Module>> INVERTER_MODULE = module("inverter_module", ModModules.INVERTER, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<Module>> SILENCE_MODULE = module("silence_module", ModModules.SILENCE, ModItems::singleWithDescription);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> WARN_MODULE = interdictionMatrixModule("warn_module", ModModules.WARN);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> BLOCK_ACCESS_MODULE = interdictionMatrixModule("block_access_module", ModModules.BLOCK_ACCESS);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> BLOCK_ALTER_MODULE = interdictionMatrixModule("block_alter_module", ModModules.BLOCK_ALTER);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> ANTI_FRIENDLY_MODULE = interdictionMatrixModule("anti_friendly_module", ModModules.ANTI_FRIENDLY);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> ANTI_HOSTILE_MODULE = interdictionMatrixModule("anti_hostile_module", ModModules.ANTI_HOSTILE);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> ANTI_PERSONNEL_MODULE = interdictionMatrixModule("anti_personnel_module", ModModules.ANTI_PERSONNEL);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> ANTI_SPAWN_MODULE = interdictionMatrixModule("anti_spawn_module", ModModules.ANTI_SPAWN);
    public static final DeferredItem<ModuleItem<InterdictionMatrixModule>> CONFISCATION_MODULE = interdictionMatrixModule("confiscation_module", ModModules.CONFISCATION);
    public static final DeferredItem<Item> BLANK_CARD = ITEMS.registerItem("blank_card", Item::new);
    public static final DeferredItem<Item> ID_CARD = ITEMS.registerItem("id_card", IdentificationCardItem::new);
    public static final DeferredItem<Item> INFINITE_POWER_CARD = ITEMS.registerItem("infinite_power_card", properties -> new BaseItem(new ExtendedItemProperties(properties.stacksTo(1)).description()));
    public static final DeferredItem<Item> FREQUENCY_CARD = ITEMS.registerItem("frequency_card", FrequencyCardItem::new);
    public static final DeferredItem<Item> STEEL_COMPOUND = ITEMS.registerItem("steel_compound", Item::new);
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.registerItem("steel_ingot", Item::new);
    public static final DeferredItem<BatteryItem> BATTERY = ITEMS.registerItem("battery", BatteryItem::new);

    public static final DeferredItem<Item> REDSTONE_TORCH_OFF = ITEMS.registerItem("redstone_torch_off", Item::new);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEM_GROUP = ITEM_GROUPS.register("main", () -> CreativeModeTab.builder()
        .title(ModUtil.translate("itemGroup", "main"))
        .icon(() -> new ItemStack(PROJECTOR_ITEM.get()))
        .displayItems((parameters, output) -> {
            for (DeferredHolder<Item, ? extends Item> entry : ITEMS.getEntries()) {
                if (entry != REDSTONE_TORCH_OFF) {
                    output.accept(entry.get());
                }
            }
        })
        .build());
    
    public static void init(IEventBus bus) {
        ITEMS.register(bus);
        ITEM_GROUPS.register(bus);
    }

    private static DeferredItem<Item> fromBlock(DeferredBlock<?> block) {
        return ITEMS.registerItem(block.getId().getPath(), properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    private static DeferredItem<ModuleItem<InterdictionMatrixModule>> interdictionMatrixModule(String name, ModuleType<InterdictionMatrixModule> module) {
        return ITEMS.registerItem(name, properties -> new InterdictionMatrixModuleItem(new ExtendedItemProperties(properties).description(), module));
    }

    private static DeferredItem<ModuleItem<Module>> module(String name, ModuleType<Module> module, Consumer<ExtendedItemProperties> props) {
        return ITEMS.registerItem(name, properties -> {
            ExtendedItemProperties ext = new ExtendedItemProperties(properties);
            props.accept(ext);
            return new ModuleItem<>(ext, module);
        });
    }

    private static DeferredItem<ProjectorModeItem> projectorMode(String name, ProjectorMode projectorMode) {
        return ITEMS.registerItem(name, properties -> new ProjectorModeItem(properties, projectorMode));
    }
    
    public static void singleWithDescription(ExtendedItemProperties properties) {
        properties.description();
        properties.getProperties().stacksTo(1);
    }

    private ModItems() {}
}
