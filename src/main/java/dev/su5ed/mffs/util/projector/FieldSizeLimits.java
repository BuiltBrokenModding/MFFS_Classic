package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public final class FieldSizeLimits {
    public static BlockPos clampFieldScale(Projector projector, Vec3i scale) {
        return new BlockPos(
            clampHorizontal(projector, scale.getX()),
            clampVertical(projector, scale.getY()),
            clampHorizontal(projector, scale.getZ())
        );
    }

    public static int clampRadius(Projector projector, int radius) {
        return clampVertical(projector, clampHorizontal(projector, radius));
    }

    public static int clampHorizontal(Projector projector, int value) {
        return clampSize(value, getHalfLimit(projector.getMaxFieldWidth()));
    }

    public static int clampVertical(Projector projector, int value) {
        return clampSize(value, getHalfLimit(projector.getMaxFieldHeight()));
    }

    public static int clampSize(int value, int limit) {
        return limit > 0 ? Math.min(value, limit) : value;
    }
    
    public static int getHalfLimit(int limit) {
        return (int) Math.ceil(limit / 2.0);
    }

    private FieldSizeLimits() {}
}
