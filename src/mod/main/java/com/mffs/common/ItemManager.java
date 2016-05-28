package com.mffs.common;

import com.mffs.MFFS;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.io.File;

/**
 * Created by pwaln on 5/28/2016.
 */
public class ItemManager {

    /* This is the tab we shall store everything in */
    public static CreativeTabs MFFS_TAB = new CreativeTabs(MFFS.MOD_NAME) {

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return (Item) Item.itemRegistry.getObject(MFFS.MODID + ":Forcicium");
        }
    };

    /**
     * Simply parses the item directory and registers them.
     */
    public static void parseItems() throws Exception{
        String dir = "com.mffs.api.items.";
        for(String file : new File((!MFFS.DEV_MODE ? "./mods/" : "./production/mod/")+dir.replace(".", "/")).list()) {
            String name = file.substring(0, file.length() - 6);
            Item item = (Item) Class.forName(dir + name).newInstance();
            item.setUnlocalizedName(name);
            item.setCreativeTab(MFFS_TAB);
            GameRegistry.registerItem(item, name);
        }
    }
}
