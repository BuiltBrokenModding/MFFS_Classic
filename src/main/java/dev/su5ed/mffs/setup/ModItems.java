package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.item.CubeProjectorModeItem;
import dev.su5ed.mffs.item.CylinderProjectorModeItem;
import dev.su5ed.mffs.item.DisintegrationModuleItem;
import dev.su5ed.mffs.item.DomeModuleItem;
import dev.su5ed.mffs.item.FusionModule;
import dev.su5ed.mffs.item.ModuleItem;
import dev.su5ed.mffs.item.ProjectorModeItem;
import dev.su5ed.mffs.item.PyramidProjectorModeItem;
import dev.su5ed.mffs.item.RemoteControllerItem;
import dev.su5ed.mffs.item.ShockModuleItem;
import dev.su5ed.mffs.item.SphereProjectorModeItem;
import dev.su5ed.mffs.item.SpongeModuleItem;
import dev.su5ed.mffs.item.StabilizationModuleItem;
import dev.su5ed.mffs.item.TubeProjectorModeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
    
    public static final RegistryObject<ModuleItem> SPEED_MODULE = ITEMS.register("speed_module", () -> new ModuleItem(itemProperties(), 1.0F).withDescription());
    public static final RegistryObject<ModuleItem> CAMOUFLAGE_MODULE = ITEMS.register("camouflage_module", () -> new ModuleItem(itemProperties().stacksTo(1), 1.5F));
    public static final RegistryObject<ModuleItem> SCALE_MODULE = ITEMS.register("scale_module", () -> new ModuleItem(itemProperties(), 1.2F).withDescription());
    public static final RegistryObject<ModuleItem> CAPACITY_MODULE = ITEMS.register("capacity_module", () -> new ModuleItem(itemProperties(), 0.5F).withDescription());
    public static final RegistryObject<ModuleItem> DISINTEGRATION_MODULE = ITEMS.register("disintegration_module", DisintegrationModuleItem::new);
    public static final RegistryObject<ModuleItem> TRANSLATION_MODULE = ITEMS.register("translation_module", () -> new ModuleItem(itemProperties(), 1.6F).withDescription());
    public static final RegistryObject<ModuleItem> ROTATION_MODULE = ITEMS.register("rotation_module", () -> new ModuleItem(itemProperties(), 0.1F).withDescription());
    public static final RegistryObject<ModuleItem> GLOW_MODULE = ITEMS.register("glow_module", () -> new ModuleItem(itemProperties()));
    public static final RegistryObject<ModuleItem> SILENCE_MODULE = ITEMS.register("silence_module", () -> new ModuleItem(itemProperties().stacksTo(1), 1).withDescription());
    public static final RegistryObject<ModuleItem> SHOCK_MODULE = ITEMS.register("shock_module", ShockModuleItem::new);
    public static final RegistryObject<ModuleItem> SPONGE_MODULE = ITEMS.register("sponge_module", SpongeModuleItem::new);
    public static final RegistryObject<ModuleItem> FUSION_MODULE = ITEMS.register("fusion_module", FusionModule::new);
    public static final RegistryObject<ModuleItem> DOME_MODULE = ITEMS.register("dome_module", DomeModuleItem::new);
    public static final RegistryObject<ModuleItem> COLLECTION_MODULE = ITEMS.register("collection_module", () -> new ModuleItem(itemProperties().stacksTo(1), 15).withDescription());
    public static final RegistryObject<ModuleItem> STABILIZATION_MODULE = ITEMS.register("stabilization_module", StabilizationModuleItem::new);

    public static final RegistryObject<ProjectorModeItem> CUBE_MODE = ITEMS.register("cube_mode", CubeProjectorModeItem::new);
    public static final RegistryObject<ProjectorModeItem> SPHERE_MODE = ITEMS.register("sphere_mode", SphereProjectorModeItem::new);
    public static final RegistryObject<ProjectorModeItem> TUBE_MODE = ITEMS.register("tube_mode", TubeProjectorModeItem::new);
    public static final RegistryObject<ProjectorModeItem> PYRAMID_MODE = ITEMS.register("pyramid_mode", PyramidProjectorModeItem::new);
    public static final RegistryObject<ProjectorModeItem> CYLINDER_MODE = ITEMS.register("cylinder_mode", CylinderProjectorModeItem::new);
    
    public static final RegistryObject<Item> REDSTONE_TORCH_OFF = ITEMS.register("redstone_torch_off", () -> new Item(new Item.Properties()));

    public static void init(final IEventBus bus) {
        ITEMS.register(bus);
    }

    private static RegistryObject<Item> fromBlock(final RegistryObject<? extends Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
    
    public static Item.Properties itemProperties() {
        return new Item.Properties().tab(ITEM_GROUP);
    }

    private ModItems() {}
}
