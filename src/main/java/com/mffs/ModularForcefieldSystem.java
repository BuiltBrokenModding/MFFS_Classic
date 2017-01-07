package com.mffs;

import com.builtbroken.mc.core.registry.ModManager;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import com.mffs.common.blocks.*;
import com.mffs.common.fluids.Fortron;
import com.mffs.common.items.ItemFocusMatrix;
import com.mffs.common.items.RemoteController;
import com.mffs.common.items.card.*;
import com.mffs.common.items.modules.interdiction.*;
import com.mffs.common.items.modules.projector.*;
import com.mffs.common.items.modules.projector.mode.*;
import com.mffs.common.items.modules.upgrades.*;
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
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = ModularForcefieldSystem.MODID, name = ModularForcefieldSystem.MOD_NAME, version = ModularForcefieldSystem.VERSION, dependencies = "required-after:VoltzEngine")
public class ModularForcefieldSystem extends AbstractMod {
    public static final String MODID = "mffs";
    public static final String VERSION = "0.40";
    public static final String MOD_NAME = "Modular Forcefield System";

    /**
     * Constructor.
     */
    public ModularForcefieldSystem() {
        super(MODID, MODID+"/general_settings");
        manager.defaultTab = new CreativeTabs(ModularForcefieldSystem.MODID) {

            @Override
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return (Item) Item.itemRegistry.getObject(ModularForcefieldSystem.MODID + ":cardBlank");
            }
        };
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

    /* Blocks, Needed for wrench */
    public static Block biometricIdentifier, coercionDeriver, forcefieldProjector, fortronCapacitor, interdictionMatrix;

    @Override
    protected void loadBlocks(ModManager manager) {
        //TODO: Change Block/Tile to VE 'Tile'
        biometricIdentifier = manager.newBlock(BlockBiometricIdentifier.class).setBlockName("biometricIdentifier");
        coercionDeriver = manager.newBlock(BlockCoercionDeriver.class).setBlockName("coercionDeriver");
        BlockForceField.BLOCK_FORCE_FIELD = (BlockForceField) manager.newBlock(BlockForceField.class).setBlockName("forceField").setCreativeTab(null);
        forcefieldProjector = manager.newBlock(BlockForceFieldProjector.class).setBlockName("forceFieldProjector");
        fortronCapacitor = manager.newBlock(BlockFortronCapacitor.class).setBlockName("fortronCapacitor");
        interdictionMatrix = manager.newBlock(BlockInterdictionMatrix.class).setBlockName("interdictionMatrix");
    }

    @Override
    public void loadItems(ModManager manager)
    {
        manager.newItem("cardBlank", ItemCardBlank.class);
        manager.newItem("cardFrequency", ItemCardFrequency.class);
        manager.newItem("cardID", ItemCardID.class);
        manager.newItem("cardInfinite", ItemCardInfinite.class);
        manager.newItem("cardLink", ItemCardLink.class);
        manager.newItem("focusMatrix", ItemFocusMatrix.class);
        manager.newItem("moduleAntiFriendly", ItemModuleAntiFriendly.class);
        manager.newItem("moduleAntiHostile", ItemModuleAntiHostile.class);
        manager.newItem("moduleAntiPersonnel", ItemModuleAntiPersonnel.class);
        manager.newItem("moduleAntiSpawn", ItemModuleAntiSpawn.class);
        manager.newItem("moduleBlockAccess", ItemModuleBlockAccess.class);
        manager.newItem("moduleBlockAlter", ItemModuleBlockAlter.class);
        manager.newItem("moduleConfiscate", ItemModuleConfiscate.class);
        manager.newItem("moduleRepulsion", ItemModuleRepulsion.class);
        manager.newItem("moduleWarn", ItemModuleWarn.class);
        manager.newItem("moduleApproximation", ItemModuleApproximation.class);
        manager.newItem("moduleArray", ItemModuleArray.class);
        manager.newItem("moduleCamouflage", ItemModuleCamouflage.class);
        manager.newItem("moduleCollection", ItemModuleCollection.class);
        manager.newItem("moduleDisintegration", ItemModuleDisintegration.class);
        manager.newItem("moduleDome", ItemModuleDome.class);
        manager.newItem("moduleFusion", ItemModuleFusion.class);
        manager.newItem("moduleGlow", ItemModuleGlow.class);
        manager.newItem("moduleInvert", ItemModuleInvert.class);
        manager.newItem("moduleShock", ItemModuleShock.class);
        manager.newItem("moduleSilence", ItemModuleSilence.class);
        manager.newItem("moduleStabilize", ItemModuleStabilize.class);
        manager.newItem("modeCube", ItemModeCube.class);
        manager.newItem("modeCustom", ItemModeCustom.class);
        manager.newItem("modeCylinder", ItemModeCylinder.class);
        manager.newItem("modePyramid", ItemModePyramid.class);
        manager.newItem("modeSphere", ItemModeSphere.class);
        manager.newItem("modeTube", ItemModeTube.class);
        manager.newItem("moduleCapacity", ItemModuleCapacity.class);
        manager.newItem("moduleRotate", ItemModuleRotate.class);
        manager.newItem("moduleScale", ItemModuleScale.class);
        manager.newItem("moduleSpeed", ItemModuleSpeed.class);
        manager.newItem("moduleTranslate", ItemModuleTranslate.class);
        manager.newItem("moduleSponge", ItemModuleSponge.class);
        manager.newItem("remoteController", RemoteController.class);
    }

    @Override
    public void loadEntities(ModManager manager) {
        GameRegistry.registerTileEntity(TileBiometricIdentifier.class, "biometricIdentifier");
        GameRegistry.registerTileEntity(TileCoercionDeriver.class, "coercionDeriver");
        GameRegistry.registerTileEntity(TileForceField.class, "forceField");
        GameRegistry.registerTileEntity(TileForceFieldProjector.class, "forceFieldProjector");
        GameRegistry.registerTileEntity(TileFortronCapacitor.class, "fortronCapacitor");
        GameRegistry.registerTileEntity(TileInterdictionMatrix.class, "interdictionMatrix");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        channel = new SimpleNetworkWrapper(MODID);
        SettingConfiguration.load();
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
