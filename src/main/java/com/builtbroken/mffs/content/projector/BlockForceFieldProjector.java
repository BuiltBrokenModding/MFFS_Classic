package com.builtbroken.mffs.content.projector;

import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mffs.MFFS;
import com.builtbroken.mffs.client.render.RenderBlockHandler;
import com.builtbroken.mffs.prefab.blocks.BlockMFFSMachine;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author Calclavia
 */
public class BlockForceFieldProjector extends BlockMFFSMachine implements IPostInit
{

    /**
     * Force field block.
     */
    public BlockForceFieldProjector()
    {
        setBlockBounds(0, 0, 0, 1, 0.8F, 1);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param p_149915_1_
     * @param p_149915_2_
     */
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileForceFieldProjector();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return RenderBlockHandler.RENDER_ID;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(MFFS.DOMAIN + ":machine");
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        return this.blockIcon;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
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
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if ((tileEntity instanceof TileForceFieldProjector))
        {
            if (((TileForceFieldProjector) tileEntity).getMode() != null)
            {
                return 10;
            }
        }

        return super.getLightValue(world, x, y, z);
    }

    @Override
    public void onPostInit()
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(this, " D ", "SSS", "FBF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'S', UniversalRecipe.PRIMARY_METAL.get(),
                'B', UniversalRecipe.BATTERY.get(),
                'D', Items.diamond);
        GameRegistry.addRecipe(recipe);
    }
}
