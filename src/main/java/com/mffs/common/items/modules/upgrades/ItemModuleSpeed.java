package com.mffs.common.items.modules.upgrades;

import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.mffs.common.items.modules.BaseModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleSpeed extends BaseModule
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "FFF", "RRR", "FFF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'R', OreNames.REDSTONE));
    }

    /**
     * Constructor.
     */
    public ItemModuleSpeed()
    {
        setCost(1F);
    }
}
