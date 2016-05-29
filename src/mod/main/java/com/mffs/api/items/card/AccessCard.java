package com.mffs.api.items.card;

import com.mffs.MFFS;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by pwaln on 5/29/2016.
 */
public class AccessCard extends Item {

    public AccessCard() {
        super();
        setHasSubtypes(true);
        setTextureName(MFFS.MODID+":AccessCard");
    }

    @Override
    public void registerIcons(IIconRegister p_94581_1_) {
        this.itemIcon = p_94581_1_.registerIcon(MFFS.MODID+":AccessCard");
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param tooltip
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List tooltip, boolean dummy) {

    }
}
