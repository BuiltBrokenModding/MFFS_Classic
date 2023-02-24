package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.world.item.ItemStack;
import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ModuleAcceptor {
    boolean hasModule(Module module);

    default int getModuleCount(Module module) {
        return getModuleCount(module, List.of());
    }

    int getModuleCount(Module module, Collection<InventorySlot> slots);

    Set<ItemStack> getModuleStacks();

    Set<Module> getModules();

    StreamEx<ItemStack> getAllModuleItemsStream();

    int getFortronCost();
}
