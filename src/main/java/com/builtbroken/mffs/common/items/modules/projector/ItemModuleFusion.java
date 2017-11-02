package com.builtbroken.mffs.common.items.modules.projector;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mffs.api.IBlockFrequency;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.fortron.FrequencyGrid;
import com.builtbroken.mffs.api.fortron.IFortronFrequency;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.TileMFFS;
import com.builtbroken.mffs.common.items.modules.BaseModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by pwaln on 6/12/2016.
 */
public class ItemModuleFusion extends BaseModule implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "FSF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'S', Item.itemRegistry.getObject("mffs:moduleShock")));
    }

    /**
     * Default Constructor used to set values.
     */
    public ItemModuleFusion()
    {
        super();
        setMaxStackSize(1);
        setCost(1.0F);
    }

    /**
     * Calls this on projection.
     *
     * @param projector   The projector interface.
     * @param fieldBlocks A set of fields that are projected.
     * @return
     */
    @Override
    public boolean onProject(IProjector projector, Set<Vector3D> fieldBlocks)
    {
        Set<IBlockFrequency> machines = FrequencyGrid.instance().get(((IFortronFrequency) projector).getFrequency());

        for (IBlockFrequency compareProjector : machines)
        {
            if (((compareProjector instanceof IProjector)) && (compareProjector != projector))
            {
                if (((TileEntity) compareProjector).getWorldObj() == ((TileEntity) projector).getWorldObj())
                {
                    if ((((TileMFFS) compareProjector).isActive()) && (((IProjector) compareProjector).getMode() != null))
                    {
                        Iterator<Vector3D> it = fieldBlocks.iterator();

                        while (it.hasNext())
                        {
                            Vector3D position = it.next();

                            if (((IProjector) compareProjector).getMode().isInField((IProjector) compareProjector, position))
                            {
                                it.remove();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
