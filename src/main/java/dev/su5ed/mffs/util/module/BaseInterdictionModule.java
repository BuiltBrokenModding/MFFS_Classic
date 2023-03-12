package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BaseInterdictionModule extends BaseModule implements InterdictionMatrixModule {

    public BaseInterdictionModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target) {
        return false;
    }
}
