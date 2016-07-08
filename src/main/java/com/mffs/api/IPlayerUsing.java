package com.mffs.api;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashSet;

/**
 * Created by pwaln on 6/1/2016.
 */
public interface IPlayerUsing {
    HashSet<EntityPlayer> getPlayersUsing();
}
