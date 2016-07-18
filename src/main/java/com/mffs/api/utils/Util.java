package com.mffs.api.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pwaln on 5/31/2016.
 */
public class Util {

    /**
     * Gets a string and seperates it based on the count parameter.
     *
     * @param s
     * @param count
     * @return
     */
    public static List<String> sepString(String s, int count) {
        List<String> list = new ArrayList<>();
        Pattern regex = Pattern.compile(".{1," + count + "}(?:\\s|$)", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(s);
        while (regexMatcher.find()) {
            list.add(regexMatcher.group());
        }
        return list;
    }

    /**
     * Takes a string and creates a new string out of a designated words per line.
     * @param string
     * @param wordsPerLine
     * @return
     */
    public static String[] splitStringPerWord(String string, int wordsPerLine) {
        return WordUtils.wrap(string, wordsPerLine).split("\\r?\\n");
    }

    /**
     * Attempts to add a itemstack into the inventory.
     *
     * @param inv
     * @param stack
     * @return
     */
    public static ItemStack addToInv_search(IInventory inv, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0)
            return null;
        //Do a loop for the exact item.
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            if (add_stack(inv.getStackInSlot(slot), stack, Math.min(inv.getInventoryStackLimit(), stack.getMaxStackSize())) && stack.stackSize <= 0)
                return null;
        }

        //Now we check for empty slots to fill.
        if (stack != null && stack.stackSize > 0) {
            for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                ItemStack item = inv.getStackInSlot(slot);
                if (item == null) {
                    inv.setInventorySlotContents(slot, stack);
                    return null;
                }
            }
        }
        return stack;
    }

    /**
     * Attempts to add a itemstack into the inventory.
     *
     * @param inv
     * @param stack
     * @return
     */
    public static ItemStack addToInv_first(IInventory inv, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0)
            return null;
        //Do a loop for the exact item.
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack item = addToInv_slot(inv, stack, slot);
            if (item == null || item.stackSize <= 0)
                return null;
        }
        return stack;
    }

    public static ItemStack addToInv_slot(IInventory inv, ItemStack stack, int slot) {
        if (stack == null || stack.stackSize <= 0)
            return null;
        ItemStack item = inv.getStackInSlot(slot);
        if (item == null) {
            inv.setInventorySlotContents(slot, stack);
            return null;
        } else if (add_stack(item, stack, Math.min(inv.getInventoryStackLimit(), item.getMaxStackSize())) && stack.stackSize <= 0) {
            return null;
        }
        return stack;
    }

    /**
     * Subtracts item 2 from Item 1.
     *
     * @param item
     * @param stack
     * @return
     */
    public static boolean add_stack(ItemStack item, ItemStack stack, int maxStackSize) {
        if (item != null && item.isItemEqual(stack) && item.isStackable()) {
            int newSize = Math.min(item.stackSize + stack.stackSize, maxStackSize);
            stack.stackSize -= (newSize - item.stackSize);
            item.stackSize = newSize;
            return true;
        }
        return false;
    }
}
