package com.mffs.mod;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = MFFS.MODID, name = "Modular Forcefields", version = MFFS.VERSION)
public class MFFS
{
    public static final String MODID = "modular_forcefield";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static final MFFS mffs_mod = new MFFS();

    @SidedProxy(clientSide="com.mffs.mod.client.ClientInit", serverSide="com.mffs.mod.api.ServerInit")
    public static SharedLoader initialize;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        initialize.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		initialize.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        initialize.postInit(event);
    }
}
