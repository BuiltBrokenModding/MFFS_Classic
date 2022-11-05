package dev.su5ed.mffs.api;

import dev.su5ed.mffs.api.security.BiometricIdentifierLink;

/**
 * Also extends IDisableable, IFortronFrequency
 * 
 * @author Calclavia
 */
public interface Projector extends BiometricIdentifierLink, IFieldInteraction
{
	/**
	 * Projects a force field.
	 */
    void projectField();

	/**
	 * Destroys a force field.
	 */
    void destroyField();

	/**
	 * @return The speed in which a force field is constructed.
	 */
    int getProjectionSpeed();

	/**
	 * * @return The amount of ticks this projector has existed in the world.
	 */
    long getTicks();

	/**
	 * DO NOT modify this list. Read-only.
	 * 
	 * @return The actual force field block coordinates in the world.
	 */
    Set<Vector3> getForceFields();

}
