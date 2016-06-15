package com.mffs;

import com.mffs.model.fluids.Fortron;
import com.mffs.model.packet.EntityToggle;
import com.mffs.model.packet.FortronSync;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = MFFS.MODID, name = MFFS.MOD_NAME, version = MFFS.VERSION)
public class MFFS {
    public static final String MODID = "mffs";
    public static final String VERSION = "1.0";
    public static final String MOD_NAME = "Modular_Forcefields";
    public static boolean DEV_MODE = true;

    @Mod.Instance
    public static MFFS mffs_mod;

    @SidedProxy(clientSide = "com.mffs.client.ClientProxy", serverSide = "com.mffs.CommonProxy")
    public static CommonProxy initialize;

    /* This is the communication channel of the mod */
    public static SimpleNetworkWrapper channel;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        channel = new SimpleNetworkWrapper(MODID);
        try {
            RegisterManager.parseItems("");
            RegisterManager.parseBlocks("");
            RegisterManager.parseEntity("");
            RegisterManager.parseFluid("");
            NetworkRegistry.INSTANCE.registerGuiHandler(this, initialize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MFFS.channel.registerMessage(EntityToggle.Handler.class, EntityToggle.class, 0, Side.SERVER);
        channel.registerMessage(FortronSync.Handler.class, FortronSync.class, 1, Side.CLIENT);
        initialize.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        initialize.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        initialize.postInit(event);
    }
}
