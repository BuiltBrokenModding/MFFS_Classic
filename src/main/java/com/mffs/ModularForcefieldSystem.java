package com.mffs;

import com.builtbroken.mc.core.registry.ModManager;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
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

@Mod(modid = ModularForcefieldSystem.MODID, name = ModularForcefieldSystem.MOD_NAME, version = ModularForcefieldSystem.VERSION, dependencies = "required-after:VoltzEngine")
public class ModularForcefieldSystem extends AbstractMod {
    public static final String MODID = "mffs";
    public static final String VERSION = "0.27";
    public static final String MOD_NAME = "Modular Forcefield System";

    /**
     * Constructor.
     */
    public ModularForcefieldSystem() {
        super(MODID, MODID+"/general_settings");
    }

    @Mod.Instance
    public static ModularForcefieldSystem modularForcefieldSystem_mod;

    @SidedProxy(clientSide = "com.mffs.client.ClientProxy", serverSide = "com.mffs.CommonProxy")
    public static CommonProxy proxy;

    /* This is the communication channel of the mod */
    public static SimpleNetworkWrapper channel;

    @Override
    public AbstractProxy getProxy() {
        return proxy;
    }

    @Override
    protected void loadBlocks(ModManager manager) {}

    @Override
    public void loadItems(ModManager manager) {}

    @Override
    public void loadEntities(ModManager manager) {}

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        channel = new SimpleNetworkWrapper(MODID);
        SettingConfiguration.load();
        try {
            //Cannot load these in methods as config isnt able to be loaded till after!
            RegisterManager.parseItems();
            RegisterManager.parseBlocks();
            RegisterManager.parseFluid();
            RegisterManager.parseEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BlockForceField.BLOCK_FORCE_FIELD = (BlockForceField) Block.getBlockFromName(ModularForcefieldSystem.MODID + ":forceField");
        MinecraftForge.EVENT_BUS.register(new ForgeSubscribeHandler());
        ModularForcefieldSystem.channel.registerMessage(EntityToggle.ServerHandler.class, EntityToggle.class, 0, Side.SERVER);
        channel.registerMessage(FortronSync.ClientHandler.class, FortronSync.class, 1, Side.CLIENT);
        ModularForcefieldSystem.channel.registerMessage(ChangeFrequency.ServerHandler.class, ChangeFrequency.class, 2, Side.SERVER);
        ModularForcefieldSystem.channel.registerMessage(ForcefieldCalculation.ClientHandler.class, ForcefieldCalculation.class, 3, Side.CLIENT);
        ModularForcefieldSystem.channel.registerMessage(BeamRequest.ClientHandler.class, BeamRequest.class, 4, Side.CLIENT);
        ModularForcefieldSystem.channel.registerMessage(ItemByteToggle.ServerHandler.class, ItemByteToggle.class, 5, Side.SERVER);
        ModularForcefieldSystem.channel.registerMessage(ItemStringToggle.ServerHandler.class, ItemStringToggle.class, 6, Side.SERVER);
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        proxy.postInit();
    }
}
