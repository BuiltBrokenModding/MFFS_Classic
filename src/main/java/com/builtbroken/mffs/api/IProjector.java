package com.builtbroken.mffs.api;

import com.builtbroken.mc.imp.transform.vector.BlockPos;
import net.minecraft.inventory.IInventory;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IProjector
        extends IInventory, IBiometricIdentifierLink, IFieldInteraction
{
    void projectField();

    void destroyField();

    int getProjectionSpeed();

    long getTicks();

    Set<BlockPos> getForceFields();
}
