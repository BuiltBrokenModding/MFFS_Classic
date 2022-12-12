package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.util.InventorySlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ModuleAcceptor {
    <T extends Item & Module> ItemStack getModule(T module);
    
    <T extends Item & Module> boolean hasModule(T module);
    
    default <T extends Item & Module> int getModuleCount(T module) {
        return getModuleCount(module, List.of());
    }

    <T extends Item & Module> int getModuleCount(T module, Collection<InventorySlot> slots);

    Set<ItemStack> getModuleStacks();

    <T extends Item & Module> Set<T> getModules();

    int getFortronCost();
}
