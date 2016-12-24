package com.mffs.common.items.modules.upgrades;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.mffs.common.items.modules.BaseModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleTranslate extends BaseModule implements IRecipeContainer {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                "FSF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'S', Item.itemRegistry.getObject("mffs:moduleScale")));
    }

    public ItemModuleTranslate() {
        setCost(2.5F);
    }
}
