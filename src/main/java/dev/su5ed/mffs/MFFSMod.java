package dev.su5ed.mffs;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.compat.MFFSProbeProvider;
import dev.su5ed.mffs.item.IdentificationCardItem;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModAttachmentTypes;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModFluids;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(MFFSMod.MODID)
public final class MFFSMod {
    public static final String MODID = "mffs";
    public static final String NAME = "MFFS";

    private static final String TOP_MODID = "theoneprobe";

    public static final Logger LOGGER = LogUtils.getLogger();

    public MFFSMod(IEventBus bus) {
        bus.addListener(this::commonSetup);
        bus.addListener(this::enqueIMC);
        bus.addListener(ModCapabilities::registerCaps);
        ModBlocks.init(bus);
        ModItems.init(bus);
        ModObjects.init(bus);
        ModMenus.init(bus);
        ModFluids.init(bus);
        ModSounds.init(bus);
        ModAttachmentTypes.init(bus);

        Network.registerPackets();

        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.CLIENT, MFFSConfig.CLIENT_SPEC);
        ctx.registerConfig(ModConfig.Type.COMMON, MFFSConfig.COMMON_SPEC);

        NeoForge.EVENT_BUS.register(ForgeEventHandler.class);
        NeoForge.EVENT_BUS.addListener(IdentificationCardItem::onLivingEntityInteract);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModObjects.initCriteriaTriggers();
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
