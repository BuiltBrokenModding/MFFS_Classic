package com.mffs.api;

import com.mffs.MFFS;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.lang.reflect.Modifier;

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
        File[] files = new File((!MFFS.DEV_MODE ? "./mods/" : "./production/mod/")+dir.replace(".", "/")).listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                continue;
            }
            String name = file.getName().substring(0, file.getName().length() - 6);
            Item item = (Item) Class.forName(dir + name).newInstance();
            if(Modifier.isAbstract(item.getClass().getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            item.setUnlocalizedName(name);
            item.setCreativeTab(MFFS_TAB);
            GameRegistry.registerItem(item, name);
        }
    }

    /**
     * Extracts a NBT tag from a item. Creates and attaches one if it does not exist.
     * @param stack
     * @return
     */
    public static NBTTagCompound getTag(ItemStack stack) {
        if(stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
