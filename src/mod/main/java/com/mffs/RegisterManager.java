package com.mffs;

import com.mffs.MFFS;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.lang.reflect.Modifier;

/**
 * Created by pwaln on 5/28/2016.
 */
public class RegisterManager {

    /* This is the tab we shall store everything in */
    public static CreativeTabs MFFS_TAB = new CreativeTabs(MFFS.MOD_NAME) {

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return (Item) Item.itemRegistry.getObject(MFFS.MODID + ":cardBlank");
        }
    };

    /**
     * Simply parses the item directory and registers them.
     */
    public static void parseItems(String offset) throws Exception{
        File[] files = new File((!MFFS.DEV_MODE ? "./mods/" : "./production/mod/")+ "com/mffs/items/" +offset.replace(".", "/")).listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                parseItems(offset+file.getName()+"/");
                continue;
            }
            String name = file.getName().substring(0, file.getName().length() - 6);
            Class rawClass = (Class) Class.forName("com.mffs.items." + offset.replace("/", ".") + name);
            if(Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            Item item = (Item) rawClass.newInstance();
            name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
            item.setUnlocalizedName(name);
            item.setTextureName(MFFS.MODID+":"+name);
            item.setCreativeTab(MFFS_TAB);
            GameRegistry.registerItem(item, name);
        }
    }

    /**
     * Simply parses the item directory and registers them.
     */
    public static void parseBlocks(String offset) throws Exception{
        File[] files = new File((!MFFS.DEV_MODE ? "./mods/" : "./production/mod/")+ "com/mffs/blocks/" +offset.replace(".", "/")).listFiles();
        if(files == null) {
            return;
        }
        for(File file : files) {
            if(file == null) continue;
            if(file.isDirectory()) {
                parseBlocks(offset+file.getName()+"/");
                continue;
            }
            String name = file.getName().substring(0, file.getName().length() - 6);
            Class rawClass = (Class) Class.forName("com.mffs.blocks." + offset.replace("/", ".") + name);
            if(Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            Block block= (Block) rawClass.newInstance();
            block.setCreativeTab(MFFS_TAB);
            block.setBlockName(name);
            GameRegistry.registerBlock(block, name);
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
