package com.mffs.api;

import com.mffs.api.vector.Vector3D;
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

    Set<Vector3D> getForceFields();
}
