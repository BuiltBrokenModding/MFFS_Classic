package com.mffs.api;

import com.mffs.api.security.IBiometricIdentifier;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IBiometricIdentifierLink {
    IBiometricIdentifier getBiometricIdentifier();

    Set<IBiometricIdentifier> getBiometricIdentifiers();
}