package com.mffs.common.blocks;

import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.mffs.common.tile.type.TileBiometricIdentifier;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author Calclavia
 */
public class BlockBiometricIdentifier extends MFFSMachine implements IPostInit
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
        return new TileBiometricIdentifier();
    }

    @Override
    public void onPostInit()
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(this, "FSF", "SCS", "FSF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'S', UniversalRecipe.PRIMARY_METAL.get(),
                'C', Item.itemRegistry.getObject("mffs:cardBlank"));
        GameRegistry.addRecipe(recipe);
    }
}
