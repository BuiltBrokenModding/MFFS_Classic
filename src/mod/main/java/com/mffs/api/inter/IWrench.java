package com.mffs.api.inter;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by pwaln on 5/30/2016.
 */
public abstract interface IWrench {
    public abstract int getSide();

    public abstract void setSide(int paramInt);

    public abstract boolean wrenchCanManipulate(EntityPlayer paramEntityPlayer, int paramInt);
}
