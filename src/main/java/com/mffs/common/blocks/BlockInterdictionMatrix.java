package com.mffs.common.blocks;

import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.mffs.ModularForcefieldSystem;
import com.mffs.client.render.RenderBlockHandler;
import com.mffs.common.tile.type.TileInterdictionMatrix;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author Calclavia
 */
public class BlockInterdictionMatrix extends MFFSMachine implements IPostInit {

    /**
     * @param world  The current world.
     * @param x      X position of block.
     * @param y      Y position of block.
     * @param z      Z position of block.
     * @param player The user.
     * @param side   The side being clicked.
     * @return
     */
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
        return new TileInterdictionMatrix();
    }

    @Override
    public void onPostInit() {
        ShapedOreRecipe recipe = new ShapedOreRecipe(this,
                "DDD", "FFF", "FEF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'D', Item.itemRegistry.getObject("mffs:moduleDisintegration"),
                'E', Blocks.ender_chest);
        GameRegistry.addRecipe(recipe);
    }
}
