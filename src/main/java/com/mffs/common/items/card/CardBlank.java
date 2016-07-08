package com.mffs.common.items.card;

import com.mffs.api.card.ICard;
import com.mffs.api.utils.Util;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Calclavia
 */
public class CardBlank extends Item implements ICard {

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param list
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        String tooltip = LanguageRegistry.instance().getStringLocalization(getUnlocalizedName() + ".tooltip");
        if (tooltip != null && tooltip.length() > 0) {
            list.addAll(Util.sepString(tooltip, 30));
        }
    }
}
