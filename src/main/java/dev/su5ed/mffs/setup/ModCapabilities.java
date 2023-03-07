package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public final class ModCapabilities {
    // BlockEntity caps
    public static final Capability<FortronStorage> FORTRON = get(new CapabilityToken<>() {});
    public static final Capability<Projector> PROJECTOR = get(new CapabilityToken<>() {});
    public static final Capability<BiometricIdentifier> BIOMETRIC_IDENTIFIER = get(new CapabilityToken<>() {});
    public static final Capability<InterdictionMatrix> INTERDICTION_MATRIX = get(new CapabilityToken<>() {});

    // Item caps
    public static final Capability<Module> MODULE = get(new CapabilityToken<>() {});
    public static final Capability<InterdictionMatrixModule> INTERDICTION_MATRIX_MODULE = get(new CapabilityToken<>() {});
    public static final Capability<ProjectorMode> PROJECTOR_MODE = get(new CapabilityToken<>() {});
    public static final Capability<IdentificationCard> IDENTIFICATION_CARD = get(new CapabilityToken<>() {});
    public static final Capability<FrequencyCard> FREQUENCY_CARD = get(new CapabilityToken<>() {});

    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(FortronStorage.class);
        event.register(Projector.class);
        event.register(BiometricIdentifier.class);

        event.register(Module.class);
        event.register(InterdictionMatrixModule.class);
        event.register(ProjectorMode.class);
        event.register(IdentificationCard.class);
    }

    private ModCapabilities() {}
}
