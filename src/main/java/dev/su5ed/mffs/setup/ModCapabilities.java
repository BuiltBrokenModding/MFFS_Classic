package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public final class ModCapabilities {
    public static final Capability<FortronStorage> FORTRON = get(new CapabilityToken<>(){});
    
    private ModCapabilities() {}
}
