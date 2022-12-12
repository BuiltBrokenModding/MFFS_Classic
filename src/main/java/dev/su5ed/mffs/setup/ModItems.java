package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.item.CubeProjectorModeItem;
import dev.su5ed.mffs.item.DisintegrationModuleItem;
import dev.su5ed.mffs.item.ModuleItem;
import dev.su5ed.mffs.item.ProjectorModeItem;
import dev.su5ed.mffs.item.RemoteControllerItem;
import dev.su5ed.mffs.item.ShockModuleItem;
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
    
    public static final RegistryObject<ModuleItem> SPEED_MODULE = ITEMS.register("speed_module", () -> new ModuleItem(itemProperties(), 1.0F).autoDescription());
    public static final RegistryObject<ModuleItem> CAMOUFLAGE_MODULE = ITEMS.register("camouflage_module", () -> new ModuleItem(itemProperties().stacksTo(1), 1.5F));
    public static final RegistryObject<ModuleItem> SCALE_MODULE = ITEMS.register("scale_module", () -> new ModuleItem(itemProperties(), 1.2F));
    public static final RegistryObject<ModuleItem> CAPACITY_MODULE = ITEMS.register("capacity_module", () -> new ModuleItem(itemProperties(), 0.5F));
    public static final RegistryObject<ModuleItem> DISINTEGRATION_MODULE = ITEMS.register("disintegration_module", DisintegrationModuleItem::new);
    public static final RegistryObject<ModuleItem> TRANSLATION_MODULE = ITEMS.register("translation_module", () -> new ModuleItem(itemProperties(), 1.6F));
    public static final RegistryObject<ModuleItem> ROTATION_MODULE = ITEMS.register("rotation_module", () -> new ModuleItem(itemProperties(), 0.1F));
    public static final RegistryObject<ModuleItem> GLOW_MODULE = ITEMS.register("glow_module", () -> new ModuleItem(itemProperties()));
    public static final RegistryObject<ModuleItem> SILENCE_MODULE = ITEMS.register("silence_module", () -> new ModuleItem(itemProperties().stacksTo(1), 1).autoDescription());
    public static final RegistryObject<ModuleItem> SHOCK_MODULE = ITEMS.register("shock_module", ShockModuleItem::new);

    public static final RegistryObject<ProjectorModeItem> CUBE_MODE = ITEMS.register("cube_mode", CubeProjectorModeItem::new);
    
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
