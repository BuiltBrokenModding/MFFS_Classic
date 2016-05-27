package com.mffs.mod.client.packethandler;

import com.mffs.mod.api.packet.ClientExample;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by pwaln on 5/26/2016.
 */
public class ClientExampleHandler implements IMessageHandler<ClientExample, /* This is a response */IMessage> {

    @Override
    public IMessage onMessage(ClientExample message, MessageContext ctx) {
        return null;
    }
}
