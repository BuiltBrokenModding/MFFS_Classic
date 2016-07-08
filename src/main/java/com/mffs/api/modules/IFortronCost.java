package com.mffs.api.modules;

/**
 * A grid MFFS uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 *
 * @author Calclavia
 */
public interface IFortronCost {
    float getFortronCost(float paramFloat);
}
