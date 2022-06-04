package dev.su5ed.mffs;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.init.ModBlocks;
import dev.su5ed.mffs.init.ModItems;
import dev.su5ed.mffs.init.ModObjects;
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

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }
}
