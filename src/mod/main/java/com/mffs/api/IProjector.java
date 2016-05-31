package com.mffs.api;

import codechicken.lib.vec.Vector3;
import net.minecraft.inventory.IInventory;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IProjector
        extends IInventory, IBiometricIdentifierLink, IFieldInteraction {
    void projectField();

    void destroyField();

    int getProjectionSpeed();

    long getTicks();

    Set<Vector3> getForceFields();
}
