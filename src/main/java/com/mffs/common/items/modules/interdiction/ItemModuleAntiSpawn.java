package com.mffs.common.items.modules.interdiction;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.mffs.common.items.modules.MatrixModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleAntiSpawn extends MatrixModule implements IRecipeContainer {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                " H ", "F F", " H ", 'H', Item.itemRegistry.getObject("mffs:moduleAntiHostile"),
                'F', Item.itemRegistry.getObject("mffs:moduleAntiFriendly")));
    }

    /**
     * Constructor.
     */
    public ItemModuleAntiSpawn() {
        setCost(10F);
    }
}
