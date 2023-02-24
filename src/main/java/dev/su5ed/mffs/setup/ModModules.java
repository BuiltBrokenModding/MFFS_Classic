package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.util.module.DisintegrationModule;
import dev.su5ed.mffs.util.module.DomeModule;
import dev.su5ed.mffs.util.module.FusionModule;
import dev.su5ed.mffs.util.module.ModuleBase;
import dev.su5ed.mffs.util.module.ShockModule;
import dev.su5ed.mffs.util.module.SpongeModule;
import dev.su5ed.mffs.util.module.StabilizationModule;

public final class ModModules {
    public static final FusionModule FUSION = new FusionModule();
    public static final ShockModule SHOCK = new ShockModule();
    public static final ModuleBase SPEED = new ModuleBase(1.0F);
    public static final ModuleBase CAMOUFLAGE = new ModuleBase(1.5F);
    public static final ModuleBase SCALE = new ModuleBase(1.2F);
    public static final ModuleBase CAPACITY = new ModuleBase();
    public static final DisintegrationModule DISINTEGRATION = new DisintegrationModule();
    public static final ModuleBase TRANSLATION = new ModuleBase(1.6F);
    public static final ModuleBase ROTATION = new ModuleBase(0.1F);
    public static final ModuleBase GLOW = new ModuleBase();
    public static final ModuleBase SILENCE = new ModuleBase(1.0F);
    public static final SpongeModule SPONGE = new SpongeModule();
    public static final DomeModule DOME = new DomeModule();
    public static final ModuleBase COLLECTION = new ModuleBase(15.0F);
    public static final StabilizationModule STABILIZAZION = new StabilizationModule();

    private ModModules() {}
}
