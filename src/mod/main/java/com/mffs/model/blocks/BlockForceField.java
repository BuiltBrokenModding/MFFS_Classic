package com.mffs.model.blocks;

import com.mffs.MFFS;
import com.mffs.api.IForceFieldBlock;
import com.mffs.api.IProjector;
import com.mffs.client.render.RenderForceFieldHandler;
import com.mffs.model.tile.type.EntityForceField;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by pwaln on 6/24/2016.
 */
public class BlockForceField extends Block implements ITileEntityProvider, IForceFieldBlock {

    public static final BlockForceField BLOCK_FORCE_FIELD = (BlockForceField) Block.getBlockFromName(MFFS.MODID + ":forceField");

    /**
     * Default method.
     */
    public BlockForceField() {
        super(Material.glass);
        setResistance(999);
        setHardness(Float.MAX_VALUE);
        setCreativeTab(null);
    }


    @Override
    public IProjector getProjector(IBlockAccess paramIBlockAccess, int paramInt1, int paramInt2, int paramInt3) {
        return null;
    }

    @Override
    public void weakenForceField(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {

    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Return true if a player with Silk Touch can harvest this block directly, and not its normal drops.
     */
    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     *
     * @param p_149745_1_
     */
    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 0;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     *
     * @param p_149720_1_
     * @param p_149720_2_
     * @param p_149720_3_
     * @param p_149720_4_
     */
    @Override
    public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_) {
        return super.colorMultiplier(p_149720_1_, p_149720_2_, p_149720_3_, p_149720_4_);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        TileEntity tile = access.getTileEntity(x, y, z);
        if (tile instanceof EntityForceField) {
            ItemStack camo = ((EntityForceField) tile).camo;
            if (camo != null) {
                //always going to be null for now.
            }
        }
        return this.getIcon(side, access.getBlockMetadata(x, y, z));
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(MFFS.MODID + ":forceField");
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @Override
    public int getRenderBlockPass() {
        return 1;
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
        return super.getLightValue(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int p_149646_5_) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof EntityForceField) {
            return true;
        }
        return super.shouldSideBeRendered(world, x, y, z, p_149646_5_);
    }

    @Override
    public int getRenderType() {
        return RenderForceFieldHandler.RENDER_ID;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param p_149915_1_
     * @param p_149915_2_
     */
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new EntityForceField();
    }
}
