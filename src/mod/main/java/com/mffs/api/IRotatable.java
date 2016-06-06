package com.mffs.api;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by pwaln on 6/1/2016.
 */
public interface IRotatable {
    ForgeDirection getDirection();

    void setDirection(ForgeDirection paramForgeDirection);
}
