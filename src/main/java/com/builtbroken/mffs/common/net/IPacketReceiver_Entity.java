package com.builtbroken.mffs.common.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
public interface IPacketReceiver_Entity
{

    IMessage handleMessage(IMessage message);
}
