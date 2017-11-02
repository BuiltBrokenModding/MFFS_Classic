package com.builtbroken.mffs.api;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by pwaln on 6/1/2016.
 */
@Deprecated
public interface IRotatable
{
    ForgeDirection getDirection();

    void setDirection(ForgeDirection paramForgeDirection);
}
