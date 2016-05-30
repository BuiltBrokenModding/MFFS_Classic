package com.mffs.api.util;

import com.mffs.api.RegisterManager;
import com.mffs.api.SecurityClearance;
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
        NBTTagCompound tag = RegisterManager.getTag(stack);
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
        return RegisterManager.getTag(stack).getInteger("validity");
    }

    /**
     * Sets the 'name' field.
     * @param stack The item to be assigned.
     * @param name The name value.
     */
    public static void setOwner(ItemStack stack, String name) {
        RegisterManager.getTag(stack).setString("name", name);
    }

    /**
     * Gets the Owner of an item.
     * @param stack The item to be parsed.
     * @return
     */
    public static String getOwner(ItemStack stack) {
        return RegisterManager.getTag(stack).getString("name");
    }

    /**
     * Gets the 'Areaname' from the stack.
     * @param stack The item.
     * @return
     */
    public static String getForcArea(ItemStack stack) {
        return RegisterManager.getTag(stack).getString("Areaname");
    }

    /**
     * Sets the ForceField Area.
     * @param stack The item.
     * @param area The area key.
     */
    public static void setForcArea(ItemStack stack, String area) {
        RegisterManager.getTag(stack).setString("Areaname", area);
    }

    /**
     * Checks if there card has security clearance.
     * @param stack
     * @param sec
     * @return
     */
    public static boolean hasClearance(ItemStack stack, SecurityClearance sec) {
        NBTTagCompound rights = RegisterManager.getTag(stack).getCompoundTag("rights");
        if(rights == null) {
            return false;
        }
        return rights.getBoolean(sec.name());
    }

    /**
     * Gives or removes clearance to a specified item.
     * @param stack The item to be checked.
     * @param sec The clearance to be operated.
     * @param value Given or removed.
     */
    public static void giveClearance(ItemStack stack, SecurityClearance sec, boolean value) {
        NBTTagCompound rights = RegisterManager.getTag(stack).getCompoundTag("rights");
        if(rights == null) {
            rights = new NBTTagCompound();
        }
        rights.setBoolean(sec.name(), value);
        RegisterManager.getTag(stack).setTag("rights", rights);
    }

}
