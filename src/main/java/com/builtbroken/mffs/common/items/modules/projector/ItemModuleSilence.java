package com.builtbroken.mffs.common.items.modules.projector;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mffs.common.items.modules.BaseModule;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleSilence extends BaseModule implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                " J ", "JFJ", " J ",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'J', Blocks.jukebox));
    }

    /**
     * Constructor.
     */
    public ItemModuleSilence()
    {
        setMaxStackSize(1);
        setCost(1F);
    }
}
