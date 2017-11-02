package com.builtbroken.mffs.content.gen;

import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mffs.ModularForcefieldSystem;
import com.builtbroken.mffs.client.render.RenderBlockHandler;
import com.builtbroken.mffs.prefab.blocks.MFFSMachine;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author Calclavia
 */
public final class BlockCoercionDeriver extends MFFSMachine implements IPostInit
{

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param p_149915_1_
     * @param p_149915_2_
     */
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileCoercionDeriver();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(ModularForcefieldSystem.MODID + ":machine");
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return RenderBlockHandler.RENDER_ID;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public void onPostInit()
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(this, "S S", "SFS", "SBS",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'S', UniversalRecipe.PRIMARY_METAL.get(),
                'B', UniversalRecipe.BATTERY.get());
        GameRegistry.addRecipe(recipe);
    }
}
