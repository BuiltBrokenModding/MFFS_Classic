package com.mffs.common.items.modules.projector;

import com.mffs.common.items.modules.BaseModule;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
public class ItemModuleShock extends BaseModule {

    /**
     * This represents the source of damage of this module!
     */
    public static final DamageSource SHOCK_SOURCE = new DamageSource("fieldShock").setDamageBypassesArmor();

    @Override
    public boolean onCollideWithForcefield(World world, int x, int y, int z, Entity entity, ItemStack moduleStack) {
        entity.attackEntityFrom(SHOCK_SOURCE, moduleStack.stackSize);
        return false;
    }
}
