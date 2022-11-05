package dev.su5ed.mffs.api.security;

import mffs.api.security.IBiometricIdentifier;

import java.util.Set;

/**
 * Applied to TileEntities that can be linked with a Biometric Identifier.
 * 
 * @author Calclavia
 * 
 */
public interface BiometricIdentifierLink
{
	public IBiometricIdentifier getBiometricIdentifier();

	public Set<IBiometricIdentifier> getBiometricIdentifiers();
}
