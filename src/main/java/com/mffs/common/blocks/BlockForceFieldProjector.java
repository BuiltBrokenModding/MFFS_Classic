package com.mffs.common.blocks;

import com.mffs.MFFS;
import com.mffs.client.render.RenderBlockHandler;
import com.mffs.common.tile.type.TileForceFieldProjector;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
public class BlockForceFieldProjector extends MFFSMachine {

    /**
     * Force field block.
     */
    public BlockForceFieldProjector() {
        setBlockBounds(0, 0, 0, 1, 0.8F, 1);
    }

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
        return new TileForceFieldProjector();
    }

    @Override
    public int getRenderType() {
        return RenderBlockHandler.RENDER_ID;
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
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Get a light value for the block at the specified coordinates, normal ranges are between 0 and 15
     *
     * @param world The current world
     * @param x     X Position
     * @param y     Y position
     * @param z     Z position
     * @return The light value
     */
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if ((tileEntity instanceof TileForceFieldProjector)) {
            if (((TileForceFieldProjector) tileEntity).getMode() != null) {
                return 10;
            }
        }

        return super.getLightValue(world, x, y, z);
    }
}
