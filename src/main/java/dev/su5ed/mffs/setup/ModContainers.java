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
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MFFSMod.MODID);

    public static final RegistryObject<MenuType<CoercionDeriverContainer>> POWERGEN_CONTAINER = CONTAINERS.register("powergen",
        () -> IForgeMenuType.create((windowId, inv, data) -> new CoercionDeriverContainer(windowId, inv.player, data.readBlockPos())));

    public static void init(final IEventBus bus) {
        CONTAINERS.register(bus);
    }

    private ModContainers() {}
}
