package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModContainers {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MFFSMod.MODID);

    public static final RegistryObject<MenuType<CoercionDeriverMenu>> COERCION_DERIVER_MENU = MENU_TYPES.register("coercion_deriver",
        () -> IForgeMenuType.create((windowId, inv, data) -> new CoercionDeriverMenu(windowId, data.readBlockPos(), inv.player, inv)));
    public static final RegistryObject<MenuType<FortronCapacitorMenu>> FORTRON_CAPACITOR_MENU = MENU_TYPES.register("fortron_capacitor",
        () -> IForgeMenuType.create((windowId, inv, data) -> new FortronCapacitorMenu(windowId, data.readBlockPos(), inv.player, inv)));

    public static void init(final IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    private ModContainers() {}
}
