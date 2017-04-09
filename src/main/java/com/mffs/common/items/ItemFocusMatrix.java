package com.mffs.common.items;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * Created by pwaln on 12/18/2016.
 */
public final class ItemFocusMatrix extends Item implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(new ItemStack(this, 9), "RSR", "SDS", "RSR", 'R', OreNames.REDSTONE, 'S', UniversalRecipe.PRIMARY_METAL.get(), 'D', OreNames.DIAMOND));
    }
}
