package mekanism.api;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by pwaln on 5/30/2016.
 */
public interface IMekWrench
{
    boolean canUseWrench(EntityPlayer player, int x, int y, int z);
}
