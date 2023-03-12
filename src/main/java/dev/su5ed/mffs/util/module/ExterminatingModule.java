package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ExterminatingModule extends BaseInterdictionModule {
    private final Predicate<LivingEntity> predicate;

    public ExterminatingModule(ModuleType<?> type, ItemStack stack, Predicate<LivingEntity> predicate) {
        super(type, stack);
        this.predicate = predicate;
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target) {
        if (this.predicate.test(target)) {
            target.hurt(ModObjects.FIELD_SHOCK, Integer.MAX_VALUE);
        }
        return false;
    }
}
