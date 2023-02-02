package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.Module;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public final class ModCapabilities {
    // BlockEntity caps
    public static final Capability<FortronStorage> FORTRON = get(new CapabilityToken<>(){});
    
    // Item caps
    public static final Capability<Module> MODULE = get(new CapabilityToken<>() {});

    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(FortronStorage.class);
        event.register(Module.class);
    }

    private ModCapabilities() {}
}
