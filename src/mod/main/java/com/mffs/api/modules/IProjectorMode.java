package com.mffs.api.modules;

import codechicken.lib.vec.Vector3;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IProjectorMode
        extends IFortronCost {
    Set<Vector3> getExteriorPoints(IFieldInteraction paramIFieldInteraction);

    Set<Vector3> getInteriorPoints(IFieldInteraction paramIFieldInteraction);

    boolean isInField(IFieldInteraction paramIFieldInteraction, Vector3 paramVector3);

    void render(IProjector paramIProjector, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat, long paramLong);
}
