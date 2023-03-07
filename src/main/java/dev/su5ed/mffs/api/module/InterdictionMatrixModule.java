package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.api.security.InterdictionMatrix;
import net.minecraft.world.entity.LivingEntity;

public interface InterdictionMatrixModule extends Module {
    /**
     * Called when the Interdiction Matrix attempts to defend a region.
     * {@return true} to stop processing other modules in this list.
     */
    boolean onDefend(InterdictionMatrix interdictionMatrix, LivingEntity target);
}
