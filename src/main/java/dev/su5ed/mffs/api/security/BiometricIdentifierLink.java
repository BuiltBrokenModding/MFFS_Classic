package dev.su5ed.mffs.api.security;

import java.util.Set;

/**
 * Applied to TileEntities that can be linked with a Biometric Identifier.
 *
 * @author Calclavia
 */
public interface BiometricIdentifierLink {
    BiometricIdentifier getBiometricIdentifier();

    Set<BiometricIdentifier> getBiometricIdentifiers();
}
