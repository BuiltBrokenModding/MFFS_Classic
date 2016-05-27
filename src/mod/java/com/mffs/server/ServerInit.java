package com.mffs.server;

import com.mffs.api.SharedLoader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by pwaln on 5/22/2016.
 */
public class ServerInit extends SharedLoader {
    /**
     * Called before the main INITIALIZE.
     * @param event Forge ModLoader event.
     */
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    /**
     * Called along with the main Initialize.
     * @param event Forge ModLoader event.
     */
    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    /**
     * Called after the main Init.
     * @param event Forge ModLoader event.
     */
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
