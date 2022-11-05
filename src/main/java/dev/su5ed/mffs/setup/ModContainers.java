package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.container.CoercionDeriverContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModContainers {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MFFSMod.MODID);

    public static final RegistryObject<MenuType<CoercionDeriverContainer>> COERCION_DERIVER_MENU = MENU_TYPES.register("coercion_deriver",
        () -> IForgeMenuType.create((windowId, inv, data) -> new CoercionDeriverContainer(windowId, data.readBlockPos(), inv.player, inv)));

    public static void init(final IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    private ModContainers() {}
}
