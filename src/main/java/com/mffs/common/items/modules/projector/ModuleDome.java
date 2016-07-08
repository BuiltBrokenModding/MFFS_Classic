package com.mffs.common.items.modules.projector;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.ItemModule;
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
