package com.mffs.common.items.modules.projector;

import com.mffs.api.IBlockFrequency;
import com.mffs.api.IProjector;
import com.mffs.api.fortron.FrequencyGrid;
import com.mffs.api.fortron.IFortronFrequency;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.TileMFFS;
import com.mffs.common.items.modules.ItemModule;
import net.minecraft.tileentity.TileEntity;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by pwaln on 6/12/2016.
 */
public class ModuleFusion extends ItemModule {

    /**
     * Default Constructor used to set values.
     */
    public ModuleFusion() {
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
    public boolean onProject(IProjector projector, Set<Vector3D> fieldBlocks) {
        Set<IBlockFrequency> machines = FrequencyGrid.instance().get(((IFortronFrequency) projector).getFrequency());

        for (IBlockFrequency compareProjector : machines) {
            if (((compareProjector instanceof IProjector)) && (compareProjector != projector)) {
                if (((TileEntity) compareProjector).getWorldObj() == ((TileEntity) projector).getWorldObj()) {
                    if ((((TileMFFS) compareProjector).isActive()) && (((IProjector) compareProjector).getMode() != null)) {
                        Iterator<Vector3D> it = fieldBlocks.iterator();

                        while (it.hasNext()) {
                            Vector3D position = it.next();

                            if (((IProjector) compareProjector).getMode().isInField((IProjector) compareProjector, position)) {
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
