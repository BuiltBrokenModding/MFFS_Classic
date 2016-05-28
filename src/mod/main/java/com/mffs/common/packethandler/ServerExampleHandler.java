package com.mffs.common.packethandler;

import com.mffs.api.packet.ServerExample;
import com.mffs.api.packet.ClientExample;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * This is a example packet. We receive the 'ServerExample' data packet from the Client, this is then interpreted and a 'ClientExample' data packet is generated
 * to sent to the Client to be handled.
 * Created by pwaln on 5/26/2016.
 */
public class ServerExampleHandler
        implements IMessageHandler</*This is the message received */ServerExample,
        /* This is a response */ClientExample> {

    @Override
    public ClientExample onMessage(ServerExample message, MessageContext ctx) {
        return null;//null represents no message return
    }
}
