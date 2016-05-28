package com.mffs.api.items;

import com.mffs.MFFS;
import com.mffs.common.ItemManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.IIcon;

/**
 * Created by pwaln on 5/28/2016.
 */
public class Forcicium extends Item {

    /**
     * Basic constructor for this item.
     */
    public Forcicium() {
        super();
        setTextureName(MFFS.MODID + ":Forcicium");
    }
}
