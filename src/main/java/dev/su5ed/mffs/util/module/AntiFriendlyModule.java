package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class AntiFriendlyModule extends BaseInterdictionModule {
    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target) {
        if (target instanceof Mob && target.getType().getCategory().isFriendly()) {
            target.hurt(ModObjects.FIELD_SHOCK, Integer.MAX_VALUE);
        }
        return false;
    }
}
