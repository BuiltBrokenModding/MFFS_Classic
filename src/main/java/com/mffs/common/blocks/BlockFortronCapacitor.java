package com.mffs.common.blocks;

import com.mffs.MFFS;
import com.mffs.client.render.RenderBlockHandler;
import com.mffs.common.tile.type.TileFortronCapacitor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
public final class BlockFortronCapacitor extends MFFSMachine {

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
        return new TileFortronCapacitor();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(MFFS.MODID + ":machine");
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return this.blockIcon;
    }

    @Override
    public int getRenderType() {
        return RenderBlockHandler.RENDER_ID;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
