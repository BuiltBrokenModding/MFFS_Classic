package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.world.item.*;
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
            return new ItemStack(Items.APPLE);
        }
    };
    private static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ITEM_GROUP);
    
    public static final RegistryObject<Item> MACHINE_ITEM = fromBlock(ModBlocks.MACHINE_BLOCK);
    public static final RegistryObject<Item> PROJECTOR_ITEM = fromBlock(ModBlocks.PROJECTOR);
    public static final RegistryObject<Item> COERCION_DERIVER_ITEM = fromBlock(ModBlocks.COERCION_DERIVER);
    
    public static final RegistryObject<Item> REDSTONE_TORCH_OFF = ITEMS.register("redstone_torch_off", () -> new Item(new Item.Properties()));

    public static void init(final IEventBus bus) {
        ITEMS.register(bus);
    }

    private static RegistryObject<Item> fromBlock(final RegistryObject<? extends Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    private ModItems() {}
}
