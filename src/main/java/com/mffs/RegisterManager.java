package com.mffs;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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

    public static List<String> getClassNames(String directory) throws IOException {
        List<String> files = new ArrayList<>();
        JarInputStream stream = new JarInputStream(new FileInputStream("./mods/" + MFFS.MODID + "-v" + MFFS.VERSION + ".jar"));
        JarEntry entry;
        while (true) {
            entry = stream.getNextJarEntry();
            if (entry == null)
                break;
            if (entry.getName().contains(".class") && entry.getName().contains(directory + "/"))
                files.add(entry.getName().replace(".class", ""));
        }
        stream.close();
        return files;
    }

    /**
     * Simply parses the item directory and registers them.
     */
    public static void parseItems() throws Exception {
        List<String> names = getClassNames("items");
        for (String name : names) {
            name = name.replace("/", ".");
            Class rawClass = (Class) Class.forName(name);
            if (Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            Item item = (Item) rawClass.newInstance();
            name = rawClass.getSimpleName();
            name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
            item.setUnlocalizedName(name);
            item.setTextureName(MFFS.MODID + ":" + name);
            item.setCreativeTab(MFFS_TAB);
            GameRegistry.registerItem(item, name);
        }
    }

    /**
     * Simply parses the item directory and registers them.
     */
    public static void parseBlocks() throws Exception {
        List<String> names = getClassNames("blocks");
        for (String name : names) {
            name = name.replace("/", ".");
            Class rawClass = (Class) Class.forName(name);
            if (Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            name = rawClass.getSimpleName().replace("Block", "");
            name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
            Block block = (Block) rawClass.newInstance();
            block.setCreativeTab(MFFS_TAB);
            block.setBlockName(name);
            GameRegistry.registerBlock(block, name);
        }
    }

    /**
     * Simply parses the item directory and registers them.
     */
    public static void parseEntity() throws Exception {
        List<String> names = getClassNames("tile");
        for (String name : names) {
            name = name.replace("/", ".");
            Class rawClass = (Class) Class.forName(name);
            if (Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            name = rawClass.getSimpleName().replace("Tile", "").substring(0, 1).toLowerCase() + name.substring(1, name.length());
            GameRegistry.registerTileEntity(rawClass, name);
        }
    }

    /**
     * Simply parses the item directory and registers them.
     */
    public static void parseFluid() throws Exception {
        List<String> names = getClassNames("fluids");
        for (String name : names) {
            name = name.replace("/", ".");
            Class rawClass = (Class) Class.forName(name);
            if (Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            Fluid fluid = (Fluid) rawClass.newInstance();
            //name = rawClass.getCanonicalName().replace("Fluid", "").substring(0, 1).toLowerCase() + name.substring(1, name.length());
            FluidRegistry.registerFluid(fluid);
        }
    }

    /**
     * Extracts a NBT tag from a item. Creates and attaches one if it does not exist.
     *
     * @param stack
     * @return
     */
    public static NBTTagCompound getTag(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
