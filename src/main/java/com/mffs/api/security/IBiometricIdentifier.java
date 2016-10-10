package com.mffs.api.security;

/**
 * @author Calclavia
 */
public interface IBiometricIdentifier {
    boolean isAccessGranted(String paramString, Permission paramPermission);
}