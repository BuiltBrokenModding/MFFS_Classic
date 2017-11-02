package com.builtbroken.mffs.common.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.item.ItemStack;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
public interface IPacketReceiver_Item
{

    /**
     * @param message
     * @param item
     * @return
     */
    IMessage handleMessage(IMessage message, ItemStack item);
}
