package com.mffs;

import akka.io.Tcp;
import com.builtbroken.mc.core.registry.ModManager;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import com.mffs.common.blocks.*;
import com.mffs.common.fluids.Fortron;
import com.mffs.common.net.packet.*;
import com.mffs.common.tile.type.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = ModularForcefieldSystem.MODID, name = ModularForcefieldSystem.MOD_NAME, version = ModularForcefieldSystem.VERSION, dependencies = "required-after:VoltzEngine")
public class ModularForcefieldSystem extends AbstractMod {
    public static final String MODID = "mffs";
    public static final String VERSION = "0.28";
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
    protected void loadBlocks(ModManager manager) {
        //TODO: Change Block/Tile to VE 'Tile'
        manager.newBlock(BlockBiometricIdentifier.class).setBlockName("biometricIdentifier").setCreativeTab(RegisterManager.MFFS_TAB);
        manager.newBlock(BlockCoercionDeriver.class).setBlockName("coercionDeriver").setCreativeTab(RegisterManager.MFFS_TAB);
        BlockForceField.BLOCK_FORCE_FIELD = (BlockForceField) manager.newBlock(BlockForceField.class).setBlockName("forceField");
        manager.newBlock(BlockForceFieldProjector.class).setBlockName("forceFieldProjector").setCreativeTab(RegisterManager.MFFS_TAB);
        manager.newBlock(BlockFortronCapacitor.class).setBlockName("fortronCapacitor").setCreativeTab(RegisterManager.MFFS_TAB);
    }

    @Override
    public void loadItems(ModManager manager) {}

    @Override
    public void loadEntities(ModManager manager) {
        GameRegistry.registerTileEntity(TileBiometricIdentifier.class, "biometricIdentifier");
        GameRegistry.registerTileEntity(TileCoercionDeriver.class, "coercionDeriver");
        GameRegistry.registerTileEntity(TileForceField.class, "forceField");
        GameRegistry.registerTileEntity(TileForceFieldProjector.class, "forceFieldProjector");
        GameRegistry.registerTileEntity(TileFortronCapacitor.class, "fortronCapacitor");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        channel = new SimpleNetworkWrapper(MODID);
        SettingConfiguration.load();
        try {
            //Cannot load these in methods as config isnt able to be loaded till after!
            RegisterManager.parseItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FluidRegistry.registerFluid(new Fortron());
        Fortron.FLUID_ID = FluidRegistry.getFluidID("fortron");
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
