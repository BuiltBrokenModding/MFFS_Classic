package com.mffs.common.blocks;

import com.mffs.common.tile.type.TileBiometricIdentifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
public class BlockBiometricIdentifier extends MFFSMachine {
    /**
     * @param world  The current world.
     * @param x      X position of block.
     * @param y      Y position of block.
     * @param z      Z position of block.
     * @param player The user.
     * @param side   The side being clicked.
     * @return
     */
    @Override
    public boolean wrenchMachine(World world, int x, int y, int z, EntityPlayer player, int side) {
        return false;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param p_149915_1_
     * @param p_149915_2_
     */
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileBiometricIdentifier();
    }
}
