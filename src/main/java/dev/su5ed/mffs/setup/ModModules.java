package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.util.module.AntiPersonnelModule;
import dev.su5ed.mffs.util.module.BaseInterdictionModule;
import dev.su5ed.mffs.util.module.BaseModule;
import dev.su5ed.mffs.util.module.DisintegrationModule;
import dev.su5ed.mffs.util.module.DomeModule;
import dev.su5ed.mffs.util.module.ExterminatingModule;
import dev.su5ed.mffs.util.module.FusionModule;
import dev.su5ed.mffs.util.module.ShockModule;
import dev.su5ed.mffs.util.module.SpongeModule;
import dev.su5ed.mffs.util.module.StabilizationModule;
import dev.su5ed.mffs.util.module.WarnModule;
import net.minecraft.world.entity.Mob;

public final class ModModules {
    public static final FusionModule FUSION = new FusionModule();
    public static final ShockModule SHOCK = new ShockModule();
    public static final BaseModule SPEED = new BaseModule(1.0F);
    public static final BaseModule CAMOUFLAGE = new BaseModule(1.5F);
    public static final BaseModule SCALE = new BaseModule(1.2F, Module.Category.FIELD, Module.Category.INTERDICTION);
    public static final BaseModule CAPACITY = new BaseModule(Module.Category.MATRIX);
    public static final DisintegrationModule DISINTEGRATION = new DisintegrationModule();
    public static final BaseModule TRANSLATION = new BaseModule(1.6F, Module.Category.FIELD);
    public static final BaseModule ROTATION = new BaseModule(0.1F, Module.Category.FIELD);
    public static final BaseModule GLOW = new BaseModule(Module.Category.MATRIX);
    public static final BaseModule SILENCE = new BaseModule(1.0F);
    public static final SpongeModule SPONGE = new SpongeModule();
    public static final DomeModule DOME = new DomeModule();
    public static final BaseModule COLLECTION = new BaseModule(15.0F);
    public static final StabilizationModule STABILIZAZION = new StabilizationModule();
    public static final BaseModule INVERTER = new BaseModule(15.0F);
    
    public static final WarnModule WARN = new WarnModule();
    public static final BaseInterdictionModule BLOCK_ACCESS = new BaseInterdictionModule(10.0F);
    public static final BaseInterdictionModule BLOCK_ALTER = new BaseInterdictionModule(15.0F);
    public static final ExterminatingModule ANTI_FRIENDLY = new ExterminatingModule(target -> target instanceof Mob && target.getType().getCategory().isFriendly());
    public static final ExterminatingModule ANTI_HOSTILE = new ExterminatingModule(target -> target instanceof Mob && !target.getType().getCategory().isFriendly());
    public static final AntiPersonnelModule ANTI_PERSONNEL = new AntiPersonnelModule();

    private ModModules() {}
}
