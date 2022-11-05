package dev.su5ed.mffs;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModContainers;
import dev.su5ed.mffs.setup.ModFluids;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MFFSMod.MODID)
public final class MFFSMod {
    public static final String MODID = "mffs";
    public static final String NAME = "MFFS";

    public static final Logger LOGGER = LogUtils.getLogger();

    public MFFSMod() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        ModBlocks.init(bus);
        ModItems.init(bus);
        ModObjects.init(bus);
        ModContainers.init(bus);
        ModFluids.init(bus);

        Network.registerPackets();
        
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.CLIENT, MFFSConfig.CLIENT_SPEC);
        ctx.registerConfig(ModConfig.Type.COMMON, MFFSConfig.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }
    
    public static ResourceLocation location(String path) {
        return new ResourceLocation(MODID, path);
    }
}
