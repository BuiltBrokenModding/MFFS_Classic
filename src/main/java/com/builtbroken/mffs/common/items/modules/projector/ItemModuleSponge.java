package com.builtbroken.mffs.common.items.modules.projector;

import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.modules.BaseModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
//TODO: Way this is done needs to be reWritten!
public class ItemModuleSponge extends BaseModule
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "WWW", "WFW", "WWW",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'W', Items.water_bucket));
    }

    /**
     * Initialize constructor so we can set attributes.
     */
    public ItemModuleSponge()
    {
        super();
        setMaxStackSize(1);
    }

    /**
     * Calls this on projection.
     *
     * @param projector The projector interface.
     * @param fields    A set of fields that are projected.
     * @return
     */
    @Override
    public boolean onProject(IProjector projector, Set<Vector3D> fields)
    {
        if (projector.getTicks() % 60L == 0L)
        {
            World world = ((TileEntity) projector).getWorldObj();

            if (!world.isRemote)
            {
                for (Vector3D point : projector.getInteriorPoints())
                {
                    Block block = point.getBlock(world);

                    if (((block instanceof BlockLiquid)) || ((block instanceof BlockFluidBase)))
                    {
                        point.setBlock(world, Blocks.air);
                    }
                }
            }
        }
        return super.onProject(projector, fields);
    }

    @Override
    public boolean requireTicks(ItemStack moduleStack)
    {
        return true;
    }
}
