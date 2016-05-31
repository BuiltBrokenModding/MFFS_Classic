package com.mffs.api.modules;

import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IModuleAcceptor {
    ItemStack getModule(IModule paramIModule);

    int getModuleCount(IModule paramIModule, int... paramVarArgs);

    Set<ItemStack> getModuleStacks(int... paramVarArgs);

    Set<IModule> getModules(int... paramVarArgs);

    int getFortronCost();
}
