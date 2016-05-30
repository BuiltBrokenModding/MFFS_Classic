package com.mffs.api.util;

import com.mffs.api.ItemManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by pwaln on 5/29/2016.
 */
public class CardUtils {

    /**
     * Validates the item with an value.
     * @param stack The item we are to validate.
     * @param min The value to be assigned.
     * @return
     */
    public static int validate(ItemStack stack, int min) {
        NBTTagCompound tag = ItemManager.getTag(stack);
        if(min > 0) {
            tag.setInteger("validity", min);
        }
        return tag.getInteger("validity");
    }

    /**
     * Gets the value assigned to this item.
     * @param stack The item to be checked.
     * @return The value assigned.
     */
    public static int validate(ItemStack stack) {
        return ItemManager.getTag(stack).getInteger("validity");
    }

    /**
     * Sets the 'name' field.
     * @param stack The item to be assigned.
     * @param name The name value.
     */
    public static void setOwner(ItemStack stack, String name) {
        ItemManager.getTag(stack).setString("name", name);
    }

    /**
     * Gets the Owner of an item.
     * @param stack The item to be parsed.
     * @return
     */
    public static String getOwner(ItemStack stack) {
        return ItemManager.getTag(stack).getString("name");
    }

    /**
     * Gets the 'Areaname' from the stack.
     * @param stack The item.
     * @return
     */
    public static String getForcArea(ItemStack stack) {
        return ItemManager.getTag(stack).getString("Areaname");
    }

    /**
     * Sets the ForceField Area.
     * @param stack The item.
     * @param area The area key.
     */
    public static void setForcArea(ItemStack stack, String area) {
        ItemManager.getTag(stack).setString("Areaname", area);
    }
}
