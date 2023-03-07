package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.BiometricIdentifierMenu;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.menu.InterdictionMatrixMenu;
import dev.su5ed.mffs.menu.ProjectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MFFSMod.MODID);

    public static final RegistryObject<MenuType<CoercionDeriverMenu>> COERCION_DERIVER_MENU = register("coercion_deriver", CoercionDeriverMenu::new);
    public static final RegistryObject<MenuType<FortronCapacitorMenu>> FORTRON_CAPACITOR_MENU = register("fortron_capacitor", FortronCapacitorMenu::new);
    public static final RegistryObject<MenuType<ProjectorMenu>> PROJECTOR_MENU = register("projector", ProjectorMenu::new);
    public static final RegistryObject<MenuType<BiometricIdentifierMenu>> BIOMETRIC_IDENTIFIER_MENU = register("biometric_identifier", BiometricIdentifierMenu::new);
    public static final RegistryObject<MenuType<InterdictionMatrixMenu>> INTERDICTION_MATRIX_MENU = register("interdiction_matrix", InterdictionMatrixMenu::new);

    public static void init(final IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuFactory<T> factory) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create((windowId, inv, data) -> factory.create(windowId, data.readBlockPos(), inv.player, inv)));
    }

    private ModMenus() {}

    private interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int windowId, BlockPos pos, Player player, Inventory inventory);
    }
}
