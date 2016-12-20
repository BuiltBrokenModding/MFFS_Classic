package com.mffs.common.items.modules.projector;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.BaseModule;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ItemModuleDome extends BaseModule implements IRecipeContainer {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                "FFF", "F F", //"F  ", "   ", "F  "
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    /**
     * Need to initialize stack size.
     */
    public ItemModuleDome() {
        super();
        setMaxStackSize(1);
    }

    /**
     * Calculates the projection field.
     *
     * @param projector   The projector interface.
     * @param fieldBlocks A set of positions.
     */
    @Override
    public void onCalculate(IFieldInteraction projector, Set<Vector3D> fieldBlocks) {
        Vector3D absoluteTranslation = new Vector3D((TileEntity) projector).add(projector.getTranslation());

        Iterator<Vector3D> it = fieldBlocks.iterator();

        while (it.hasNext()) {
            Vector3D pos = it.next();

            if (pos.y < absoluteTranslation.y) {
                it.remove();
            }
        }
    }
}
