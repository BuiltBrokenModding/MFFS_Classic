package com.mffs.common.items.modules.projector;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.mffs.common.items.modules.BaseModule;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleCollection extends BaseModule implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "F F", " H ", "F F",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'H', Blocks.hopper));
    }

    /**
     * Constructor.
     */
    public ItemModuleCollection()
    {
        setMaxStackSize(1);
        setCost(15F);
    }
}
