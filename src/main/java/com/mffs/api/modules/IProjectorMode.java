package com.mffs.api.modules;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.api.vector.Vector3D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IProjectorMode
        extends IFortronCost {
    Set<Vector3D> getExteriorPoints(IFieldInteraction paramIFieldInteraction);

    Set<Vector3D> getInteriorPoints(IFieldInteraction paramIFieldInteraction);

    boolean isInField(IFieldInteraction paramIFieldInteraction, Vector3D paramVector3);

    @SideOnly(Side.CLIENT)
    void render(IProjector paramIProjector, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat, long paramLong);
}
