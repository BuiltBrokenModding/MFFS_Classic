package dev.su5ed.mffs.setup;

// =============================================================================
// 1.12.2 Backport: Capability registration
// =============================================================================

import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.capabilities.CapabilityInject;

@Mod.EventBusSubscriber(modid = "mffs")
public final class ModCapabilities {

    // Capability tokens - injected by Forge via @CapabilityInject after registration
    @CapabilityInject(FortronStorage.class)
    public static Capability<FortronStorage> FORTRON = null;

    @CapabilityInject(Projector.class)
    public static Capability<Projector> PROJECTOR = null;

    @CapabilityInject(BiometricIdentifier.class)
    public static Capability<BiometricIdentifier> BIOMETRIC_IDENTIFIER = null;

    @CapabilityInject(InterdictionMatrix.class)
    public static Capability<InterdictionMatrix> INTERDICTION_MATRIX = null;

    @CapabilityInject(ModuleType.class)
    public static Capability<ModuleType> MODULE_TYPE = null;

    @CapabilityInject(ProjectorMode.class)
    public static Capability<ProjectorMode> PROJECTOR_MODE = null;

    @CapabilityInject(IdentificationCard.class)
    public static Capability<IdentificationCard> IDENTIFICATION_CARD = null;

    @CapabilityInject(FrequencyCard.class)
    public static Capability<FrequencyCard> FREQUENCY_CARD = null;

    /**
     * Call from {@link dev.su5ed.mffs.MFFSMod#preInit} to register all MFFS capabilities.
     * TileEntities/Items expose capabilities via ICapabilityProvider.getCapability().
     * The IStorage implementations are no-ops since we handle serialization in TileEntity NBT.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register() {
        registerNoOp(FortronStorage.class);
        registerNoOp(Projector.class);
        registerNoOp(BiometricIdentifier.class);
        registerNoOp(InterdictionMatrix.class);
        registerNoOp(ModuleType.class);
        registerNoOp(ProjectorMode.class);
        registerNoOp(IdentificationCard.class);
        registerNoOp(FrequencyCard.class);
    }

    /**
     * Registers a capability with no-op IStorage and null factory.
     * Storage is handled by TileEntity NBT; items handle their own capability data.
     */
    @SuppressWarnings("unchecked")
    private static <T> void registerNoOp(Class<T> capClass) {
        CapabilityManager.INSTANCE.register(capClass, new net.minecraftforge.common.capabilities.Capability.IStorage<T>() {
            @Override
            public net.minecraft.nbt.NBTBase writeNBT(Capability<T> capability, T instance, net.minecraft.util.EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<T> capability, T instance, net.minecraft.util.EnumFacing side, net.minecraft.nbt.NBTBase nbt) {
            }
        }, () -> null);
    }

    private ModCapabilities() {}
}
