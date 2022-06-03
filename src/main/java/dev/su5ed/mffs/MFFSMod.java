package dev.su5ed.mffs;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MFFSMod.MODID)
public final class MFFSMod {
    public static final String MODID = "mffs";
    
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public MFFSMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModObjects.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }
}
