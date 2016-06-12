package com.mffs.model.items.modules.projector;

import com.mffs.model.items.modules.ItemModule;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
public class ModuleShock extends ItemModule {

    /**
     * This represents the source of damage of this module!
     */
    private static final DamageSource SHOCK_SOURCE = new DamageSource("fieldShock").setDamageBypassesArmor();

    @Override
    public boolean onCollideWithForceField(World world, int x, int y, int z, Entity entity, ItemStack moduleStack) {
        entity.attackEntityFrom(SHOCK_SOURCE, moduleStack.stackSize);
        return false;
    }
}
