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
public class ItemModuleGlow extends BaseModule implements IRecipeContainer {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                "GGG", "GFG", "GGG",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'G', Items.glowstone_dust));
    }
}
