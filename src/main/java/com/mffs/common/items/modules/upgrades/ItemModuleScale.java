package com.mffs.common.items.modules.upgrades;

import com.mffs.common.items.modules.BaseModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleScale extends BaseModule {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                "F F",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    /**
     * Constructor.
     */
    public ItemModuleScale() {
        setCost(2.5F);
    }
}
