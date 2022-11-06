package dev.su5ed.mffs.api.module;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface ModuleAcceptor {
    <T extends Item & Module> ItemStack getModule(T module);

    <T extends Item & Module> int getModuleCount(T module, int... slots);

    Set<ItemStack> getModuleStacks(int... slots);

    <T extends Item & Module> Set<T> getModules(int... slots);

    int getFortronCost();
}
