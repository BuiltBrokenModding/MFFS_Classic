package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.item.BaseItem;
import dev.su5ed.mffs.item.BaseItem.ExtendedItemProperties;
import dev.su5ed.mffs.item.BatteryItem;
import dev.su5ed.mffs.item.BiometricIdentifierItem;
import dev.su5ed.mffs.item.IdentificationCardItem;
import dev.su5ed.mffs.item.InterdictionMatrixModuleItem;
import dev.su5ed.mffs.item.ModuleItem;
import dev.su5ed.mffs.item.ProjectorModeItem;
import dev.su5ed.mffs.item.RemoteControllerItem;
import dev.su5ed.mffs.util.module.AntiPersonnelModule;
import dev.su5ed.mffs.util.module.BaseInterdictionModule;
import dev.su5ed.mffs.util.module.ConfiscationModule;
import dev.su5ed.mffs.util.module.ExterminatingModule;
import dev.su5ed.mffs.util.module.WarnModule;
import dev.su5ed.mffs.util.projector.ModProjectorModes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public final class ModItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MFFSMod.MODID);

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MFFSMod.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(PROJECTOR_ITEM.get());
        }
    };
    private static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ITEM_GROUP);

    public static final RegistryObject<Item> PROJECTOR_ITEM = fromBlock(ModBlocks.PROJECTOR);
    public static final RegistryObject<Item> COERCION_DERIVER_ITEM = fromBlock(ModBlocks.COERCION_DERIVER);
    public static final RegistryObject<Item> FORTRON_CAPACITOR_ITEM = fromBlock(ModBlocks.FORTRON_CAPACITOR);
    public static final RegistryObject<Item> BIOMETRIC_IDENTIFIER_ITEM = ITEMS.register("biometric_identifier", BiometricIdentifierItem::new);
    public static final RegistryObject<Item> INTERDICTION_MATRIX_ITEM = fromBlock(ModBlocks.INTERDICTION_MATRIX);
    public static final RegistryObject<Item> REMOTE_CONTROLLER_ITEM = ITEMS.register("remote_controller", RemoteControllerItem::new);

    public static final RegistryObject<Item> FOCUS_MATRIX = ITEMS.register("focus_matrix", ModItems::simpleItem);
    public static final RegistryObject<ProjectorModeItem> CUBE_MODE = projectorMode("cube_mode", ModProjectorModes.CUBE);
    public static final RegistryObject<ProjectorModeItem> SPHERE_MODE = projectorMode("sphere_mode", ModProjectorModes.SPHERE);
    public static final RegistryObject<ProjectorModeItem> TUBE_MODE = projectorMode("tube_mode", ModProjectorModes.TUBE);
    public static final RegistryObject<ProjectorModeItem> PYRAMID_MODE = projectorMode("pyramid_mode", ModProjectorModes.PYRAMID);
    public static final RegistryObject<ProjectorModeItem> CYLINDER_MODE = projectorMode("cylinder_mode", ModProjectorModes.CYLINDER);
    public static final RegistryObject<ModuleItem<Module>> TRANSLATION_MODULE = module("translation_module", ModModules.TRANSLATION, ExtendedItemProperties::description);
    public static final RegistryObject<ModuleItem<Module>> SCALE_MODULE = module("scale_module", ModModules.SCALE, ExtendedItemProperties::description);
    public static final RegistryObject<ModuleItem<Module>> ROTATION_MODULE = module("rotation_module", ModModules.ROTATION, ExtendedItemProperties::description);
    public static final RegistryObject<ModuleItem<Module>> SPEED_MODULE = module("speed_module", ModModules.SPEED, ExtendedItemProperties::description);
    public static final RegistryObject<ModuleItem<Module>> CAPACITY_MODULE = module("capacity_module", ModModules.CAPACITY);
    public static final RegistryObject<ModuleItem<Module>> FUSION_MODULE = module("fusion_module", ModModules.FUSION, singleStack());
    public static final RegistryObject<ModuleItem<Module>> DOME_MODULE = module("dome_module", ModModules.DOME);
    public static final RegistryObject<ModuleItem<Module>> CAMOUFLAGE_MODULE = module("camouflage_module", ModModules.CAMOUFLAGE, singleStack());
    public static final RegistryObject<ModuleItem<Module>> DISINTEGRATION_MODULE = module("disintegration_module", ModModules.DISINTEGRATION, singleStack());
    public static final RegistryObject<ModuleItem<Module>> SHOCK_MODULE = module("shock_module", ModModules.SHOCK);
    public static final RegistryObject<ModuleItem<Module>> GLOW_MODULE = module("glow_module", ModModules.GLOW);
    public static final RegistryObject<ModuleItem<Module>> SPONGE_MODULE = module("sponge_module", ModModules.SPONGE, singleStack());
    public static final RegistryObject<ModuleItem<Module>> STABILIZATION_MODULE = module("stabilization_module", ModModules.STABILIZAZION, singleStack(), ExtendedItemProperties::description);
    public static final RegistryObject<ModuleItem<Module>> COLLECTION_MODULE = module("collection_module", ModModules.COLLECTION, singleStack());
    public static final RegistryObject<ModuleItem<Module>> INVERTER_MODULE = module("inverter_module", ModModules.INVERTER, singleStack(), ExtendedItemProperties::description);
    public static final RegistryObject<ModuleItem<Module>> SILENCE_MODULE = module("silence_module", ModModules.SILENCE, singleStack(), ExtendedItemProperties::description);
    public static final RegistryObject<InterdictionMatrixModuleItem<WarnModule>> WARN_MODULE = interdictionMatrixModule("warn_module", ModModules.WARN);
    public static final RegistryObject<InterdictionMatrixModuleItem<BaseInterdictionModule>> BLOCK_ACCESS_MODULE = interdictionMatrixModule("block_access_module", ModModules.BLOCK_ACCESS);
    public static final RegistryObject<InterdictionMatrixModuleItem<BaseInterdictionModule>> BLOCK_ALTER_MODULE = interdictionMatrixModule("block_alter_module", ModModules.BLOCK_ALTER);
    public static final RegistryObject<InterdictionMatrixModuleItem<ExterminatingModule>> ANTI_FRIENDLY_MODULE = interdictionMatrixModule("anti_friendly_module", ModModules.ANTI_FRIENDLY);
    public static final RegistryObject<InterdictionMatrixModuleItem<ExterminatingModule>> ANTI_HOSTILE_MODULE = interdictionMatrixModule("anti_hostile_module", ModModules.ANTI_HOSTILE);
    public static final RegistryObject<InterdictionMatrixModuleItem<AntiPersonnelModule>> ANTI_PERSONNEL_MODULE = interdictionMatrixModule("anti_personnel_module", ModModules.ANTI_PERSONNEL);
    public static final RegistryObject<InterdictionMatrixModuleItem<BaseInterdictionModule>> ANTI_SPAWN_MODULE = interdictionMatrixModule("anti_spawn_module", ModModules.ANTI_SPAWN);
    public static final RegistryObject<InterdictionMatrixModuleItem<ConfiscationModule>> CONFISCATION_MODULE = interdictionMatrixModule("confiscation_module", ModModules.CONFISCATION);
    public static final RegistryObject<Item> BLANK_CARD = ITEMS.register("blank_card", ModItems::simpleItem);
    public static final RegistryObject<Item> ID_CARD = ITEMS.register("id_card", IdentificationCardItem::new);
    public static final RegistryObject<Item> INFINITE_POWER_CARD = ITEMS.register("infinite_power_card", () -> new BaseItem(new ExtendedItemProperties(itemProperties().stacksTo(1)).description()));
    public static final RegistryObject<Item> STEEL_COMPOUND = ITEMS.register("steel_compound", ModItems::simpleItem);
    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", ModItems::simpleItem);
    public static final RegistryObject<BatteryItem> BATTERY = ITEMS.register("battery", BatteryItem::new);

    public static final RegistryObject<Item> REDSTONE_TORCH_OFF = ITEMS.register("redstone_torch_off", () -> new Item(new Item.Properties()));

    public static void init(final IEventBus bus) {
        ITEMS.register(bus);
    }

    private static RegistryObject<Item> fromBlock(final RegistryObject<? extends Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    private static RegistryObject<ModuleItem<Module>> module(String name, Module module) {
        return module(name, module, new ExtendedItemProperties(itemProperties()));
    }

    private static RegistryObject<ModuleItem<Module>> module(String name, Module module, ExtendedItemProperties properties) {
        return module(name, module, properties, item -> {});
    }

    private static RegistryObject<ModuleItem<Module>> module(String name, Module module, Consumer<BaseItem.ExtendedItemProperties> consumer) {
        return module(name, module, new ExtendedItemProperties(itemProperties()), consumer);
    }

    private static <T extends Module & InterdictionMatrixModule> RegistryObject<InterdictionMatrixModuleItem<T>> interdictionMatrixModule(String name, T module) {
        return ITEMS.register(name, () -> new InterdictionMatrixModuleItem<>(new ExtendedItemProperties(itemProperties()).description(), module));
    }

    private static RegistryObject<ModuleItem<Module>> module(String name, Module module, ExtendedItemProperties properties, Consumer<BaseItem.ExtendedItemProperties> consumer) {
        return ITEMS.register(name, () -> {
            consumer.accept(properties);
            return new ModuleItem<>(properties, module);
        });
    }

    private static RegistryObject<ProjectorModeItem> projectorMode(String name, ProjectorMode projectorMode) {
        return ITEMS.register(name, () -> new ProjectorModeItem(itemProperties(), projectorMode));
    }

    public static Item simpleItem() {
        return new Item(itemProperties());
    }

    public static ExtendedItemProperties singleStack() {
        return new ExtendedItemProperties(itemProperties().stacksTo(1));
    }

    public static Item.Properties itemProperties() {
        return new Item.Properties().tab(ITEM_GROUP);
    }

    private ModItems() {}
}
