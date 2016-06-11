package com.mffs.model.items.modeConf;

import codechicken.lib.vec.Vector3;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.IProjector;
import com.mffs.model.items.ItemMode;

import java.util.Set;

/**
 * Created by pwaln on 6/9/2016.
 */
public class ModeCustom extends ItemMode {
    @Override
    public Set<Vector3> getExteriorPoints(IFieldInteraction paramIFieldInteraction) {
        return null;
    }

    @Override
    public Set<Vector3> getInteriorPoints(IFieldInteraction paramIFieldInteraction) {
        return null;
    }

    @Override
    public boolean isInField(IFieldInteraction paramIFieldInteraction, Vector3 paramVector3) {
        return false;
    }

    @Override
    public void render(IProjector paramIProjector, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat, long paramLong) {

    }
}
