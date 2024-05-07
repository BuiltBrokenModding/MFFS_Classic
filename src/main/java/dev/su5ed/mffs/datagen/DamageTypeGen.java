package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

public final class DamageTypeGen {

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(ModObjects.FIELD_SHOCK_TYPE, new DamageType("mffs.field_shock", 0.1F));
    }
    
    private DamageTypeGen() {}
}
