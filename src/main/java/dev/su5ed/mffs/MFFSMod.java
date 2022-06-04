package dev.su5ed.mffs;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModContainers;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MFFSMod.MODID)
public final class MFFSMod {
    public static final String MODID = "mffs";

    public static final Logger LOGGER = LogUtils.getLogger();

    public MFFSMod() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        ModBlocks.init(bus);
        ModItems.init(bus);
        ModObjects.init();
        ModContainers.init(bus);

        Network.registerPackets();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }
}
