package dev.su5ed.mffs.setup;

// =============================================================================
// 1.12.2 Backport: TileEntity, Particle, and Advancement registration
// =============================================================================

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModObjects {

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(ProjectorBlockEntity.class,           new ResourceLocation(MFFSMod.MODID, "projector"));
        GameRegistry.registerTileEntity(CoercionDeriverBlockEntity.class,     new ResourceLocation(MFFSMod.MODID, "coercion_deriver"));
        GameRegistry.registerTileEntity(FortronCapacitorBlockEntity.class,    new ResourceLocation(MFFSMod.MODID, "fortron_capacitor"));
        GameRegistry.registerTileEntity(ForceFieldBlockEntity.class,          new ResourceLocation(MFFSMod.MODID, "force_field"));
        GameRegistry.registerTileEntity(BiometricIdentifierBlockEntity.class, new ResourceLocation(MFFSMod.MODID, "biometric_identifier"));
        GameRegistry.registerTileEntity(InterdictionMatrixBlockEntity.class,  new ResourceLocation(MFFSMod.MODID, "interdiction_matrix"));
    }

    // DamageSource string key - used when creating DamageSource("mffs.field_shock")
    public static final String FIELD_SHOCK_DAMAGE_TYPE = MFFSMod.MODID + ".field_shock";

    private ModObjects() {}
}
