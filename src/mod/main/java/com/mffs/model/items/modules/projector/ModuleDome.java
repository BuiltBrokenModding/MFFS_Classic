package com.mffs.model.items.modules.projector;

import codechicken.lib.vec.Vector3;
import com.mffs.api.IFieldInteraction;
import com.mffs.model.items.modules.ItemModule;
import net.minecraft.tileentity.TileEntity;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by pwaln on 6/12/2016.
 */
public class ModuleDome extends ItemModule {

    /**
     * Need to initialize stack size.
     */
    public ModuleDome() {
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
    public void onCalculate(IFieldInteraction projector, Set<Vector3> fieldBlocks) {
        Vector3 absoluteTranslation = Vector3.fromTileEntity((TileEntity) projector).add(projector.getTranslation());

        Iterator<Vector3> it = fieldBlocks.iterator();

        while (it.hasNext()) {
            Vector3 pos = it.next();

            if (pos.y < absoluteTranslation.y) {
                it.remove();
            }
        }
    }
}
