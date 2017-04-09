package com.mffs.common.items.modules.projector;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.mffs.common.items.modules.BaseModule;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleShock extends BaseModule implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "FWF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'W', UniversalRecipe.WIRE.get()));
    }

    /**
     * This represents the source of damage of this module!
     */
    public static final DamageSource SHOCK_SOURCE = new DamageSource("fieldShock").setDamageBypassesArmor();

    @Override
    public boolean onCollideWithForcefield(World world, int x, int y, int z, Entity entity, ItemStack moduleStack)
    {
        entity.attackEntityFrom(SHOCK_SOURCE, moduleStack.stackSize);
        return false;
    }
}
