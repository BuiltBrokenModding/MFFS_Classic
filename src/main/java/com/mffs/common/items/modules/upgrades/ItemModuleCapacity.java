package com.mffs.common.items.modules.upgrades;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.mffs.common.items.modules.BaseModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleCapacity extends BaseModule implements IRecipeContainer {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                "FBF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'B', UniversalRecipe.BATTERY.get()));
    }

    /**
     * Constructor.
     */
    public ItemModuleCapacity() {
        //setMaxStackSize(1);
        setCost(1F);
    }
}
