package com.mffs.api.items.card;

import com.mffs.MFFS;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

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


}
