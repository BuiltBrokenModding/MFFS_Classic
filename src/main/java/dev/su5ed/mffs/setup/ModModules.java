package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.util.module.AntiPersonnelModule;
import dev.su5ed.mffs.util.module.BaseInterdictionModule;
import dev.su5ed.mffs.util.module.BaseModule;
import dev.su5ed.mffs.util.module.ConfiscationModule;
import dev.su5ed.mffs.util.module.DisintegrationModule;
import dev.su5ed.mffs.util.module.DomeModule;
import dev.su5ed.mffs.util.module.ExterminatingModule;
import dev.su5ed.mffs.util.module.FusionModule;
import dev.su5ed.mffs.util.module.ShockModule;
import dev.su5ed.mffs.util.module.SpongeModule;
import dev.su5ed.mffs.util.module.StabilizationModule;
import dev.su5ed.mffs.util.module.WarnModule;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class ModModules {
    public static final ModuleType<Module> FUSION = create(FusionModule::new, 1.0F);
    public static final ModuleType<Module> SHOCK = create(ShockModule::new, 1.0F);
    public static final ModuleType<Module> SPEED = create(1.0F);
    public static final ModuleType<Module> CAMOUFLAGE = create(1.5F);
    public static final ModuleType<Module> SCALE = create(1.2F, Module.Category.FIELD, Module.Category.INTERDICTION);
    public static final ModuleType<Module> CAPACITY = create(Module.Category.MATRIX);
    public static final ModuleType<Module> DISINTEGRATION = create(DisintegrationModule::new, 20);
    public static final ModuleType<Module> TRANSLATION = create(1.6F, Module.Category.FIELD);
    public static final ModuleType<Module> ROTATION = create(0.1F, Module.Category.FIELD);
    public static final ModuleType<Module> GLOW = create(Module.Category.MATRIX);
    public static final ModuleType<Module> SILENCE = create(1.0F);
    public static final ModuleType<Module> SPONGE = create(SpongeModule::new, 1.0F);
    public static final ModuleType<Module> DOME = create(DomeModule::new, 0.5F, Module.Category.MATRIX);
    public static final ModuleType<Module> COLLECTION = create(15.0F);
    public static final ModuleType<Module> STABILIZAZION = create(StabilizationModule::new, 20.0F);
    public static final ModuleType<Module> INVERTER = create(15.0F);

    public static final ModuleType<InterdictionMatrixModule> WARN = createInterdiction(WarnModule::new);
    public static final ModuleType<InterdictionMatrixModule> BLOCK_ACCESS = createInterdiction(10.0F);
    public static final ModuleType<InterdictionMatrixModule> BLOCK_ALTER = createInterdiction(15.0F);
    public static final ModuleType<InterdictionMatrixModule> ANTI_FRIENDLY = createInterdiction((type, stack) -> new ExterminatingModule(type, stack, target -> target instanceof Mob && target.getType().getCategory().isFriendly()));
    public static final ModuleType<InterdictionMatrixModule> ANTI_HOSTILE = createInterdiction((type, stack) -> new ExterminatingModule(type, stack, target -> target instanceof Mob && !target.getType().getCategory().isFriendly()));
    public static final ModuleType<InterdictionMatrixModule> ANTI_PERSONNEL = createInterdiction(AntiPersonnelModule::new);
    public static final ModuleType<InterdictionMatrixModule> ANTI_SPAWN = createInterdiction(10.0F);
    public static final ModuleType<InterdictionMatrixModule> CONFISCATION = createInterdiction(ConfiscationModule::new);

    private ModModules() {}

    private static ModuleType<Module> create(ModuleFactory<Module> factory, float fortronCost) {
        return new BaseModuleType<>(factory, fortronCost, Module.Category.MATRIX);
    }

    private static ModuleType<Module> create(Module.Category... categories) {
        return create(BaseModule::new, 0.5F, categories);
    }

    private static ModuleType<Module> create(float fortronCost, Module.Category... categories) {
        return create(BaseModule::new, fortronCost, categories);
    }

    private static ModuleType<Module> create(ModuleFactory<Module> factory, float fortronCost, Module.Category... categories) {
        return new BaseModuleType<>(factory, fortronCost, categories);
    }

    private static ModuleType<Module> create(float fortronCost) {
        return new BaseModuleType<>(BaseModule::new, fortronCost);
    }

    private static ModuleType<InterdictionMatrixModule> createInterdiction(float fortronCost) {
        return createInterdiction(BaseInterdictionModule::new, fortronCost);
    }

    private static ModuleType<InterdictionMatrixModule> createInterdiction(ModuleFactory<InterdictionMatrixModule> factory) {
        return createInterdiction(factory, 0.5F);
    }

    private static ModuleType<InterdictionMatrixModule> createInterdiction(ModuleFactory<InterdictionMatrixModule> factory, float fortronCost) {
        return new BaseModuleType<>(factory, fortronCost, Module.Category.INTERDICTION);
    }

    public static class BaseModuleType<T extends Module> implements ModuleType<T> {
        private final float fortronCost;
        private final Set<Module.Category> categories;
        private final ModuleFactory<T> factory;

        public BaseModuleType(ModuleFactory<T> factory, float fortronCost, Module.Category... categories) {
            this.factory = factory;
            this.fortronCost = fortronCost;
            this.categories = Set.of(categories);
        }

        @Override
        public float getFortronCost(float amplifier) {
            return this.fortronCost;
        }

        @Override
        public Set<Module.Category> getCategories() {
            return this.categories;
        }

        @Override
        public T createModule(ItemStack stack) {
            return this.factory.create(this, stack);
        }
    }
    
    public interface ModuleFactory<T extends Module> {
        T create(ModuleType<T> type, ItemStack stack);
    }
}
