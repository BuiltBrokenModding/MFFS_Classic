package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Base implementation of {@link InterdictionMatrixModule} with no-op defense.
 * 1.12.2 Backport: LivingEntity→EntityLivingBase.
 */
public class BaseInterdictionModule extends BaseModule implements InterdictionMatrixModule {

    public BaseInterdictionModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, EntityLivingBase target) {
        return false;
    }
}
