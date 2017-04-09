package com.mffs.api.modules;

import com.mffs.api.security.IInterdictionMatrix;
import net.minecraft.entity.Entity;

/**
 * A grid ModularForcefieldSystem uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 * /**
 *
 * @author Calclavia
 */
public interface IInterdictionModule extends IModule
{
    boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, Entity paramEntityLivingBase);
}
