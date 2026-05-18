package dev.su5ed.mffs.util;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves a dye {@link ItemStack} to its 0xRRGGBB color via the Forge Ore Dictionary,
 * supporting modded dyes that register under the standard {@code dye<Color>} ore names.
 */
public final class DyeHelper {
    /** Cached ore-dict IDs for each of the 16 vanilla dye colors → 0xRRGGBB. */
    private static final Map<Integer, Integer> DYE_ORE_IDS = new HashMap<>();

    static {
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            String oreName = "dye" + capitalize(dye.getName());
            DYE_ORE_IDS.put(OreDictionary.getOreID(oreName), dye.getColorValue());
        }
    }

    private DyeHelper() {}

    /**
     * @return the 0xRRGGBB color of the dye represented by {@code stack},
     *         or {@code -1} if the stack is empty or not a recognized dye.
     */
    public static int getDyeColor(ItemStack stack) {
        if (stack.isEmpty()) return -1;
        int[] oreIds = OreDictionary.getOreIDs(stack);
        if (oreIds.length == 0) return -1;
        for (int id : oreIds) {
            Integer color = DYE_ORE_IDS.get(id);
            if (color != null) return color;
        }
        return -1;
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
