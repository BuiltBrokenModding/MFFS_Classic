package com.builtbroken.mffs.common.items.modules.upgrades;

import com.builtbroken.jlib.data.Colors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Calclavia
 */
@Deprecated //Rotation will be supported via GUI in the future
public class ItemModuleRotate extends Item
{
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy)
    {
        list.add(Colors.RED.code + "No longer used");
        list.add(Colors.RED.code + "Right click to recover item cost");
    }
}
