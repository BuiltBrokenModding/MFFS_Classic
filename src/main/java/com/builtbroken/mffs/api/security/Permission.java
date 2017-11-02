package com.builtbroken.mffs.api.security;


/**
 * @author Calclavia
 */
@Deprecated //Will be moved to VoltzEngine's permission system
public enum Permission
{
    WARP,
    BLOCK_PLACE_ACCESS,
    BLOCK_ACCESS,
    CONFIGURE,
    BYPASS_DEFENSE,
    BYPASS_CONFISCATION,
    REMOTE_CONTROL;

    /**
     * @param id
     * @return
     */
    public static Permission getPerm(int id)
    {
        if (id < Permission.values().length)
        {
            return Permission.values()[id];
        }
        return Permission.REMOTE_CONTROL;
    }
}