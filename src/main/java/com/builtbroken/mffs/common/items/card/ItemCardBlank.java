package com.builtbroken.mffs.common.items.card;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mffs.api.card.ICard;
import com.builtbroken.mffs.api.utils.Util;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemCardBlank extends Item implements ICard, IRecipeContainer
{

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param list
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy)
    {
        String tooltip = LanguageRegistry.instance().getStringLocalization(getUnlocalizedName() + ".tooltip");
        if (tooltip != null && tooltip.length() > 0)
        {
            list.addAll(Util.sepString(tooltip, 30));
        }
    }

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this, "PPP", "PSP", "PPP", 'P', Items.paper, 'S', UniversalRecipe.PRIMARY_METAL.get()));
    }
}
