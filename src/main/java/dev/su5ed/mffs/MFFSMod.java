package dev.su5ed.mffs;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.compat.MFFSProbeProvider;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModFluids;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MFFSMod.MODID)
public final class MFFSMod {
    public static final String MODID = "mffs";
    public static final String NAME = "MFFS";

    private static final String TOP_MODID = "theoneprobe";

    public static final Logger LOGGER = LogUtils.getLogger();

    public MFFSMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::enqueIMC);
        bus.addListener(ModCapabilities::registerCaps);
        ModBlocks.init(bus);
        ModItems.init(bus);
        ModObjects.init(bus);
        ModMenus.init(bus);
        ModFluids.init(bus);
        ModSounds.init(bus);

        Network.registerPackets();

        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.CLIENT, MFFSConfig.CLIENT_SPEC);
        ctx.registerConfig(ModConfig.Type.COMMON, MFFSConfig.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(ForgeEventHandler.class);
    }

    private void enqueIMC(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded(TOP_MODID)) {
            InterModComms.sendTo(TOP_MODID, "getTheOneProbe", MFFSProbeProvider::new);
        }
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MODID, path);
    }
}
