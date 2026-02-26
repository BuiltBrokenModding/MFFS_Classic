package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MFFSMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CoercionDeriverMenu>> COERCION_DERIVER_MENU = register("coercion_deriver", CoercionDeriverMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<FortronCapacitorMenu>> FORTRON_CAPACITOR_MENU = register("fortron_capacitor", FortronCapacitorMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ProjectorMenu>> PROJECTOR_MENU = register("projector", ProjectorMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<BiometricIdentifierMenu>> BIOMETRIC_IDENTIFIER_MENU = register("biometric_identifier", BiometricIdentifierMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<InterdictionMatrixMenu>> INTERDICTION_MATRIX_MENU = register("interdiction_matrix", InterdictionMatrixMenu::new);

    public static void init(IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, MenuFactory<T> factory) {
        return MENU_TYPES.register(name, () -> IMenuTypeExtension.create((windowId, inv, data) -> factory.create(windowId, data.readBlockPos(), inv.player, inv)));
    }

    private ModMenus() {}

    private interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int windowId, BlockPos pos, Player player, Inventory inventory);
    }
}
