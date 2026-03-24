package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.util.DamageSource;

/**
 * Custom {@link DamageSource} for kills dealt by the Interdiction Matrix's
 * Anti-Friendly and Anti-Hostile modules.  Carries a reference to the
 * originating {@link InterdictionMatrix} so that the {@code LivingDropsEvent}
 * handler can check for a Collection module and route drops.
 */
public class InterdictionDamageSource extends DamageSource {
    private final InterdictionMatrix interdictionMatrix;

    public InterdictionDamageSource(InterdictionMatrix interdictionMatrix) {
        super(ModObjects.INTERDICTION_DAMAGE_TYPE);
        this.interdictionMatrix = interdictionMatrix;
    }

    public InterdictionMatrix getInterdictionMatrix() {
        return this.interdictionMatrix;
    }
}
