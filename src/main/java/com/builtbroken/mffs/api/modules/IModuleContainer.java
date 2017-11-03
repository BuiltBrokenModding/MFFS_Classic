package com.builtbroken.mffs.api.modules;

import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Inventory that contains modules
 */
public interface IModuleContainer
{
    @Deprecated
    int getModuleCount(Class<? extends IFieldModule> paramIModule, int... slots); //TODO phase out class with id

    Set<ItemStack> getModuleStacks(int... slots);

    Set<IFieldModule> getModules(int... slots);
}
