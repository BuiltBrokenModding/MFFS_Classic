package com.mffs.common.blocks;

import com.mffs.MFFS;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.Random;

/**
 * Original MFFS File.
 * Credits: Thunderdark, Calclavia
 */
public class MonazitOre extends Block {

    public MonazitOre() {
        super(Material.rock);
        setHardness(3.0F);
        setResistance(5.0F);
        setBlockTextureName(MFFS.MODID+":MonazitOre");
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     *
     * @param rGen
     */
    @Override
    public int quantityDropped(Random rGen) {
        return 1;
    }


}
