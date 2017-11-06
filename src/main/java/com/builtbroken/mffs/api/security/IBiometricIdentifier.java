package com.builtbroken.mffs.api.security;

/**
 * @author Calclavia
 */
@Deprecated //Being replace by permission system
public interface IBiometricIdentifier
{
    boolean isAccessGranted(String paramString, Permission paramPermission);
}