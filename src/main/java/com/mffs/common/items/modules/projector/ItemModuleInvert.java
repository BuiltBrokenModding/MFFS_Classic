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
public class ItemModuleInvert extends BaseModule implements IRecipeContainer {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                "L  ", "F  ", "L  ",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'L', Blocks.lapis_block));
    }

    /**
     * Constructor.
     */
    public ItemModuleInvert() {
        setMaxStackSize(1);
        setCost(15F);
    }
}
