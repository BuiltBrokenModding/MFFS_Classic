package com.mffs;

import com.builtbroken.mc.core.registry.ModManager;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.File;
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

    private static final String REGEX_MATCH = "^["+ModularForcefieldSystem.MODID +"].+";

    public static List<String> getClassNames(String directory) throws IOException {
        List<String> files = new ArrayList<>();
        //TODO: Remove all this shit.
        //Use regex to match the .jar name
        File dir = new File((!SettingConfiguration.DEV_MODE ? "./mods/" : "./libs/"));
        String file = null;
        if(dir != null && dir.isDirectory()) {
            String[] fNames = dir.list();
            for(String f : fNames) {
                if(f.matches(REGEX_MATCH)) {
                    file = f;
                    break;
                }
            }
        }
        JarInputStream stream = new JarInputStream(new FileInputStream((!SettingConfiguration.DEV_MODE ? "./mods/" : "./libs/")+file));
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
    public static void parseItems(ModManager manager) throws Exception {
        List<String> names = getClassNames("common/items");
        for (String name : names) {
            name = name.replace("/", ".");
            Class rawClass = Class.forName(name);
            if (Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            name = rawClass.getSimpleName().replace("Item", "");
            name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
            System.out.println("manager.newItem(" + name + ", " + rawClass + ");");
            manager.newItem(name, rawClass);
        }
    }

    /**
     * Simply parses the item directory and registers them.
     */
    @Deprecated
    public static void parseBlocks() throws Exception {
        List<String> names = getClassNames("common/blocks");
        for (String name : names) {
            name = name.replace("/", ".");
            Class rawClass = (Class) Class.forName(name);
            if (Modifier.isAbstract(rawClass.getModifiers())) { //This is a abstract class and we simply override it in others!
                continue;
            }
            name = rawClass.getSimpleName().replace("Block", "");
            name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
            Block block = (Block) rawClass.newInstance();
            block.setCreativeTab(ModularForcefieldSystem.modularForcefieldSystem_mod.getManager().defaultTab);
            block.setBlockName(name);
            GameRegistry.registerBlock(block, name);
        }
    }

    /**
     * Simply parses the item directory and registers them.
     */
    @Deprecated
    public static void parseEntity() throws Exception {
        List<String> names = getClassNames("common/tile");

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
    @Deprecated
    public static void parseFluid() throws Exception {
        List<String> names = getClassNames("common/fluids");
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
