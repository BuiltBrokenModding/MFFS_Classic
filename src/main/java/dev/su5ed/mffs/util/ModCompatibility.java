package dev.su5ed.mffs.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public final class ModCompatibility {
    
    public static boolean isEnergyItem(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }
    
    private ModCompatibility() {}
}
