package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.*;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = MFFSMod.MODID)
public final class ModBlocks {

    // Static block references - populated during RegistryEvent.Register<Block>
    public static ProjectorBlock PROJECTOR;
    public static CoercionDeriverBlock COERCION_DERIVER;
    public static FortronCapacitorBlock FORTRON_CAPACITOR;
    public static ForceFieldBlockImpl FORCE_FIELD;
    public static BiometricIdentifierBlock BIOMETRIC_IDENTIFIER;
    public static InterdictionMatrixBlock INTERDICTION_MATRIX;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        PROJECTOR             = register(registry, new ProjectorBlock(), "projector");
        COERCION_DERIVER      = register(registry, new CoercionDeriverBlock(), "coercion_deriver");
        FORTRON_CAPACITOR     = register(registry, new FortronCapacitorBlock(), "fortron_capacitor");
        FORCE_FIELD           = register(registry, new ForceFieldBlockImpl(), "force_field");
        BIOMETRIC_IDENTIFIER  = register(registry, new BiometricIdentifierBlock(), "biometric_identifier");
        INTERDICTION_MATRIX   = register(registry, new InterdictionMatrixBlock(), "interdiction_matrix");
    }

    private static <T extends Block> T register(IForgeRegistry<Block> registry, T block, String name) {
        block.setRegistryName(MFFSMod.MODID, name);
        block.setTranslationKey(MFFSMod.MODID + "." + name);
        registry.register(block);
        return block;
    }

    private ModBlocks() {}
}
