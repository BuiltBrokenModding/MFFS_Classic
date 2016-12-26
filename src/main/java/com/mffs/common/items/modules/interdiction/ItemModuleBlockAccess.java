package com.mffs.common.items.modules.interdiction;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.mffs.common.items.modules.MatrixModule;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleBlockAccess extends MatrixModule implements IRecipeContainer {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                " C ", "IFI", " C ",
                'C', Blocks.chest,
                'I', Blocks.iron_block,
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    /**
     * Constructor.
     */
    public ItemModuleBlockAccess() {
        setMaxStackSize(1);
        setCost(10F);
    }
}
