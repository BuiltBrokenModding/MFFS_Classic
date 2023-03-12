package dev.su5ed.mffs.api.module;

import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface ModuleType<T extends Module> {
    /**
     * The amount of Fortron this module consumes per tick.
     */
    float getFortronCost(float amplifier);
    
    Set<Module.Category> getCategories();
    
    T createModule(ItemStack stack);
}
