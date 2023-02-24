package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.item.BatteryItem;
import dev.su5ed.mffs.item.ModuleItem;
import dev.su5ed.mffs.item.ProjectorModeItem;
import dev.su5ed.mffs.item.RemoteControllerItem;
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
    public static final RegistryObject<Item> REMOTE_CONTROLLER_ITEM = ITEMS.register("remote_controller", RemoteControllerItem::new);

    public static final RegistryObject<ModuleItem> SPEED_MODULE = module("speed_module", ModModules.SPEED, ModuleItem::withDescription);
    public static final RegistryObject<ModuleItem> CAMOUFLAGE_MODULE = module("camouflage_module", ModModules.CAMOUFLAGE, singleStack());
    public static final RegistryObject<ModuleItem> SCALE_MODULE = module("scale_module", ModModules.SCALE, ModuleItem::withDescription);
    public static final RegistryObject<ModuleItem> CAPACITY_MODULE = module("capacity_module", ModModules.CAPACITY);
    public static final RegistryObject<ModuleItem> DISINTEGRATION_MODULE = module("disintegration_module", ModModules.DISINTEGRATION, singleStack());
    public static final RegistryObject<ModuleItem> TRANSLATION_MODULE = module("translation_module", ModModules.TRANSLATION, ModuleItem::withDescription);
    public static final RegistryObject<ModuleItem> ROTATION_MODULE = module("rotation_module", ModModules.ROTATION, ModuleItem::withDescription);
    public static final RegistryObject<ModuleItem> GLOW_MODULE = module("glow_module", ModModules.GLOW);
    public static final RegistryObject<ModuleItem> SILENCE_MODULE = module("silence_module", ModModules.SILENCE, singleStack(), ModuleItem::withDescription);
    public static final RegistryObject<ModuleItem> SHOCK_MODULE = module("shock_module", ModModules.SHOCK);
    public static final RegistryObject<ModuleItem> SPONGE_MODULE = module("sponge_module", ModModules.SPONGE, singleStack());
    public static final RegistryObject<ModuleItem> FUSION_MODULE = module("fusion_module", ModModules.FUSION, singleStack());
    public static final RegistryObject<ModuleItem> DOME_MODULE = module("dome_module", ModModules.DOME);
    public static final RegistryObject<ModuleItem> COLLECTION_MODULE = module("collection_module", ModModules.COLLECTION, singleStack());
    public static final RegistryObject<ModuleItem> STABILIZATION_MODULE = module("stabilization_module", ModModules.STABILIZAZION, singleStack(), ModuleItem::withDescription);
    public static final RegistryObject<ModuleItem> INVERTER_MODULE = module("inverter_module", ModModules.INVERTER, singleStack(), ModuleItem::withDescription);

    public static final RegistryObject<ProjectorModeItem> CUBE_MODE = projectorMode("cube_mode", ModProjectorModes.CUBE);
    public static final RegistryObject<ProjectorModeItem> SPHERE_MODE = projectorMode("sphere_mode", ModProjectorModes.SPHERE);
    public static final RegistryObject<ProjectorModeItem> TUBE_MODE = projectorMode("tube_mode", ModProjectorModes.TUBE);
    public static final RegistryObject<ProjectorModeItem> PYRAMID_MODE = projectorMode("pyramid_mode", ModProjectorModes.PYRAMID);
    public static final RegistryObject<ProjectorModeItem> CYLINDER_MODE = projectorMode("cylinder_mode", ModProjectorModes.CYLINDER);

    public static final RegistryObject<Item> FOCUS_MATRIX = ITEMS.register("focus_matrix", ModItems::simpleItem);
    public static final RegistryObject<BatteryItem> BATTERY = ITEMS.register("battery", BatteryItem::new);
    public static final RegistryObject<Item> STEEL_COMPOUND = ITEMS.register("steel_compound", ModItems::simpleItem);
    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", ModItems::simpleItem);

    public static final RegistryObject<Item> REDSTONE_TORCH_OFF = ITEMS.register("redstone_torch_off", () -> new Item(new Item.Properties()));

    public static void init(final IEventBus bus) {
        ITEMS.register(bus);
    }

    private static RegistryObject<Item> fromBlock(final RegistryObject<? extends Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    private static RegistryObject<ModuleItem> module(String name, Module module) {
        return module(name, module, itemProperties());
    }

    private static RegistryObject<ModuleItem> module(String name, Module module, Item.Properties properties) {
        return module(name, module, properties, item -> {});
    }

    private static RegistryObject<ModuleItem> module(String name, Module module, Consumer<ModuleItem> consumer) {
        return module(name, module, itemProperties(), consumer);
    }

    private static RegistryObject<ModuleItem> module(String name, Module module, Item.Properties properties, Consumer<ModuleItem> consumer) {
        return ITEMS.register(name, () -> {
            ModuleItem item = new ModuleItem(properties, module);
            consumer.accept(item);
            return item;
        });
    }
    
    private static RegistryObject<ProjectorModeItem> projectorMode(String name, ProjectorMode projectorMode) {
        return ITEMS.register(name, () -> new ProjectorModeItem(itemProperties(), projectorMode));
    }
    
    public static Item simpleItem() {
        return new Item(itemProperties());
    }

    public static Item.Properties singleStack() {
        return itemProperties().stacksTo(1);
    }

    public static Item.Properties itemProperties() {
        return new Item.Properties().tab(ITEM_GROUP);
    }

    private ModItems() {}
}
