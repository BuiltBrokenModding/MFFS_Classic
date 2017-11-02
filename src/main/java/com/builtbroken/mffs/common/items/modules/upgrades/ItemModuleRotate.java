package com.builtbroken.mffs.common.items.modules.upgrades;

import com.builtbroken.mffs.common.items.modules.BaseModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleRotate extends BaseModule
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "F  ", " F ", "  F",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    /**
     * Constructor.
     */
    public ItemModuleRotate()
    {
        setCost(0.5F);
    }
}
