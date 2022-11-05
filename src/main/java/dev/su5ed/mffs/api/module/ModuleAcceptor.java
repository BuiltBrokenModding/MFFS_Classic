package dev.su5ed.mffs.api.module;

import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface ModuleAcceptor {
    ItemStack getModule(Module module);

    int getModuleCount(Module module, int... slots);

    Set<ItemStack> getModuleStacks(int... slots);

    Set<Module> getModules(int... slots);

    int getFortronCost();
}
