package dev.su5ed.mffs;

import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.proxy.CommonProxy;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.FrequencyGrid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import dev.su5ed.mffs.command.MffsCommand;
import dev.su5ed.mffs.compat.MFFSProbeProvider;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(
    modid   = MFFSMod.MODID,
    name    = MFFSMod.NAME,
    version = MFFSMod.VERSION,
    dependencies = "required-after:forge@[14.23.5.2847,);after:patchouli"
)
public final class MFFSMod {
    public static final String MODID   = "mffs";
    public static final String NAME    = "Modular Force Field System";
    public static final String VERSION = "${mod_version}";

    private static final String TOP_MODID = "theoneprobe";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Instance
    public static MFFSMod INSTANCE;

    @SidedProxy(
        clientSide = "dev.su5ed.mffs.proxy.ClientProxy",
        serverSide = "dev.su5ed.mffs.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Load config from disk
        MFFSConfig.load(event.getSuggestedConfigurationFile());

        // Register capabilities
        ModCapabilities.register();

        // Register TileEntity classes
        ModObjects.registerTileEntities();

        // Register event handlers on the Forge event bus
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFFSGuiHandler());

        // Setup network channel
        Network.init();

        // Optional TheOneProbe integration
        if (Loader.isModLoaded(TOP_MODID)) {
            FMLInterModComms.sendFunctionMessage(TOP_MODID, "getTheOneProbe",
                MFFSProbeProvider.class.getName());
        }

        // 1.12 crafting recipes use OreDictionary for steel ingots (only if not disabled).
        if (!MFFSConfig.disableSteelItems) {
            OreDictionary.registerOre(ModTags.INGOTS_STEEL, ModItems.STEEL_INGOT);
            GameRegistry.addSmelting(ModItems.STEEL_COMPOUND, new ItemStack(ModItems.STEEL_INGOT), 0.5F);
            GameRegistry.addShapedRecipe(
                new ResourceLocation(MODID, "steel_compound"),
                null,
                new ItemStack(ModItems.STEEL_COMPOUND),
                " C ", "CIC", " C ",
                'C', new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE),
                'I', new ItemStack(Items.IRON_INGOT));
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new MffsCommand());
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        // Reset the FrequencyGrid before world/chunk loading to prevent stale references.
        FrequencyGrid.reinitiate();
    }

    /**
     * Creates a {@link ResourceLocation} namespaced to this mod.
     */
    public static ResourceLocation location(String path) {
        return new ResourceLocation(MODID, path);
    }
}
