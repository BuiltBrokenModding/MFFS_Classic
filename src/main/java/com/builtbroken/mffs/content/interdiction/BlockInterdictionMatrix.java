package com.builtbroken.mffs.content.interdiction;

import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mffs.prefab.blocks.MFFSMachine;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author Calclavia
 */
public class BlockInterdictionMatrix extends MFFSMachine implements IPostInit
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
        return new TileInterdictionMatrix();
    }

    @Override
    public void onPostInit()
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(this,
                "DDD", "FFF", "FEF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'D', Item.itemRegistry.getObject("mffs:moduleDisintegration"),
                'E', Blocks.ender_chest);
        GameRegistry.addRecipe(recipe);
    }
}
