package com.builtbroken.mffs.api;

import com.builtbroken.mc.imp.transform.vector.BlockPos;
import net.minecraft.inventory.IInventory;

import java.util.Set;

/**
 * @author Calclavia
 */
@Deprecated //Useless
public interface IProjector
        extends IInventory, IBiometricIdentifierLink, IFieldInteraction
{
    @Deprecated //Shouldn't expose
    void projectField();

    @Deprecated //Shouldn't expose
    void destroyField();

    @Deprecated //Shouldn't expose
    int getProjectionSpeed();

    @Deprecated //Shouldn't expose
    long getTicks();

    @Deprecated //Convert to field object
    Set<BlockPos> getForceFields();
}
