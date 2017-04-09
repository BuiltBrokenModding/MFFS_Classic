package com.mffs.common.items.modules.projector;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.mffs.common.items.modules.BaseModule;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleApproximation extends BaseModule implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                " A ", "AFA", " A ",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'A', Items.golden_axe));
    }

    /**
     * Constructor.
     */
    public ItemModuleApproximation()
    {
        setMaxStackSize(1);
        setCost(1F);
    }
}
