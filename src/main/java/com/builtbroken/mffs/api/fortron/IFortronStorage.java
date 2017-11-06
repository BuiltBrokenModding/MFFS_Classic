package com.builtbroken.mffs.api.fortron;

/**
 * A grid ModularForcefieldSystem uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 *
 * @author Calclavia
 */
@Deprecated //Use fluid tank system
public interface IFortronStorage
{
    int getFortronEnergy();

    void setFortronEnergy(int paramInt);

    int getFortronCapacity();

    int requestFortron(int paramInt, boolean paramBoolean);

    int provideFortron(int paramInt, boolean paramBoolean);
}
