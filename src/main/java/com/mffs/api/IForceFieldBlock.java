package com.mffs.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
public interface IForceFieldBlock {
    IProjector getProjector(IBlockAccess paramIBlockAccess, int paramInt1, int paramInt2, int paramInt3);

    void weakenForceField(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}