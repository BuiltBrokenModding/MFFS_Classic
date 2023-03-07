package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import net.minecraft.world.entity.LivingEntity;

public class BaseInterdictionModule extends BaseModule implements InterdictionMatrixModule {

    public BaseInterdictionModule() {
        super(Category.INTERDICTION);
    }

    public BaseInterdictionModule(float fortronCost) {
        super(fortronCost, Category.INTERDICTION);
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target) {
        return false;
    }
}
