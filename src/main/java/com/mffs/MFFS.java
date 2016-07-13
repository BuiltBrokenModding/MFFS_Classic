package com.mffs;

import com.mffs.common.blocks.BlockForceField;
import com.mffs.common.net.packet.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = MFFS.MODID, name = MFFS.MOD_NAME, version = MFFS.VERSION, dependencies = "required-after:Mekanism")
public class MFFS {
    public static final String MODID = "mffs";
    public static final String VERSION = "0.25";
    public static final String MOD_NAME = "Modular_Forcefields";

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
            RegisterManager.parseItems();
            RegisterManager.parseBlocks();
            RegisterManager.parseEntity();
            RegisterManager.parseFluid();
            NetworkRegistry.INSTANCE.registerGuiHandler(this, initialize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BlockForceField.BLOCK_FORCE_FIELD = (BlockForceField) Block.getBlockFromName(MFFS.MODID + ":forceField");
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
        MFFS.channel.registerMessage(EntityToggle.ServerHandler.class, EntityToggle.class, 0, Side.SERVER);
        channel.registerMessage(FortronSync.ClientHandler.class, FortronSync.class, 1, Side.CLIENT);
        MFFS.channel.registerMessage(ChangeFrequency.ServerHandler.class, ChangeFrequency.class, 2, Side.SERVER);
        MFFS.channel.registerMessage(ForcefieldCalculation.ClientHandler.class, ForcefieldCalculation.class, 3, Side.CLIENT);
        MFFS.channel.registerMessage(ChangeTransferMode.ClientHandler.class, ChangeTransferMode.class, 4, Side.CLIENT);
        MFFS.channel.registerMessage(BeamRequest.ClientHandler.class, BeamRequest.class, 5, Side.CLIENT);
        initialize.preInit(event);
        ModConfiguration.load();
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
