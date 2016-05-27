package com.mffs.mod;

import com.mffs.mod.api.SharedLoader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import static com.mffs.mod.MFFS.MOD_NAME;

@Mod(modid = MFFS.MODID, name = MFFS.MOD_NAME, version = MFFS.VERSION)
public class MFFS
{
    public static final String MODID = "modular_forcefield";
    public static final String VERSION = "1.0";
    public static final String MOD_NAME = "Modular_Forcefields";

    @Mod.Instance
    public static final MFFS mffs_mod = new MFFS();

    @SidedProxy(clientSide="com.mffs.mod.client.ClientInit", serverSide="com.mffs.mod.server.ServerInit")
    public static SharedLoader initialize;

    /* This is the communication channel of the mod */
    public static SimpleNetworkWrapper channel;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        channel = new SimpleNetworkWrapper(MODID);
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
