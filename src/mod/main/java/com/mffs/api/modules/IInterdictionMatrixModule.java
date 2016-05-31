package com.mffs.api.modules;

import com.mffs.api.security.IInterdictionMatrix;
import net.minecraft.entity.EntityLivingBase;

/**
 * A grid MFFS uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 * /**
 *
 * @author Calclavia
 */
public interface IInterdictionMatrixModule
        extends IModule {
    boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, EntityLivingBase paramEntityLivingBase);
}
