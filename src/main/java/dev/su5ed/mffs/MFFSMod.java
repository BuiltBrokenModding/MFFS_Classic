package dev.su5ed.mffs;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.item.IdentificationCardItem;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.*;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(MFFSMod.MODID)
public final class MFFSMod {
    public static final String MODID = "mffs";
    public static final String NAME = "MFFS";

    public static final Logger LOGGER = LogUtils.getLogger();

    public MFFSMod(IEventBus bus, ModContainer container) {
        bus.addListener(ModCapabilities::registerCaps);
        bus.addListener(Network::registerPackets);
        ModBlocks.init(bus);
        ModItems.init(bus);
        ModObjects.init(bus);
        ModMenus.init(bus);
        ModFluids.init(bus);
        ModSounds.init(bus);
        ModDataComponentTypes.init(bus);

        container.registerConfig(ModConfig.Type.CLIENT, MFFSConfig.CLIENT_SPEC);
        container.registerConfig(ModConfig.Type.COMMON, MFFSConfig.COMMON_SPEC);

        NeoForge.EVENT_BUS.register(ModEventHandler.class);
        NeoForge.EVENT_BUS.addListener(IdentificationCardItem::onLivingEntityInteract);
    }

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
