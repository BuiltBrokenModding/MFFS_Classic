package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.function.Predicate;

public class ExterminatingModule extends BaseInterdictionModule {
    private final Predicate<LivingEntity> predicate;

    public ExterminatingModule(Predicate<LivingEntity> predicate) {
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
