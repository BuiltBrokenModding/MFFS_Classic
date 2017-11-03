package com.builtbroken.mffs.api.modules;

import com.builtbroken.mffs.api.security.IInterdictionMatrix;
import net.minecraft.entity.Entity;

/**
 * A grid ModularForcefieldSystem uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 * /**
 *
 * @author Calclavia
 */
public interface IInterdictionModule extends IFieldModule
{
    boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, Entity paramEntityLivingBase);
}
