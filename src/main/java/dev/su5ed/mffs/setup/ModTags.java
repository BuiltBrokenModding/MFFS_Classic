package dev.su5ed.mffs.setup;

// =============================================================================
// 1.12.2 Backport: Tag / OreDictionary
// =============================================================================

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ModTags {

    // Item OreDictionary names
    public static final String FORTRON_FUEL  = "fortronFuel";

    public static final String INGOTS_STEEL  = "ingotSteel";

    private static Set<Block> FORCEFIELD_REPLACEABLE;

    /**
     * Get the set of blocks that force fields can replace when projecting.
     * Includes snow, vines, tall/short grass, dead bushes, etc.
     * Lazily initialized to avoid class loading issues.
     */
    public static Set<Block> getForceFieldReplaceable() {
        if (FORCEFIELD_REPLACEABLE == null) {
            Set<Block> set = new HashSet<>();
            set.add(Blocks.SNOW_LAYER);
            set.add(Blocks.SNOW);
            set.add(Blocks.VINE);
            set.add(Blocks.TALLGRASS);
            set.add(Blocks.DEADBUSH);
            set.add(Blocks.DOUBLE_PLANT);
            set.add(Blocks.WATERLILY);
            FORCEFIELD_REPLACEABLE = Collections.unmodifiableSet(set);
        }
        return FORCEFIELD_REPLACEABLE;
    }


    private ModTags() {}
}
