package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.world.item.ItemStack;
import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ModuleAcceptor {
    boolean hasModule(ModuleType<?> module);

    default int getModuleCount(ModuleType<?> module) {
        return getModuleCount(module, List.of());
    }

    int getModuleCount(ModuleType<?> module, Collection<InventorySlot> slots);

    Set<ItemStack> getModuleStacks();

    Set<ModuleType<?>> getModules();
    
    Set<Module> getModuleInstances();

    StreamEx<ItemStack> getAllModuleItemsStream();

    int getFortronCost();
}
