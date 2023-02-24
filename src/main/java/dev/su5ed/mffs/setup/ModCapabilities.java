package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ProjectorMode;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public final class ModCapabilities {
    // BlockEntity caps
    public static final Capability<FortronStorage> FORTRON = get(new CapabilityToken<>() {});
    public static final Capability<Projector> PROJECTOR = get(new CapabilityToken<>() {});

    // Item caps
    public static final Capability<Module> MODULE = get(new CapabilityToken<>() {});
    public static final Capability<ProjectorMode> PROJECTOR_MODE = get(new CapabilityToken<>() {});

    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(FortronStorage.class);
        event.register(Projector.class);

        event.register(Module.class);
        event.register(ProjectorMode.class);
    }

    private ModCapabilities() {}
}
