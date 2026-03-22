package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public class TubeProjectorMode extends CubeProjectorMode {
    TubeProjectorMode() {}

    @Override
    public Set<Vec3d> getExteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();
        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();
        // Top and bottom faces — step 0.5 prevents holes after any rotation
        for (double x = -negScale.getX(); x <= posScale.getX(); x += 0.5) {
            for (double z = -negScale.getZ(); z <= posScale.getZ(); z += 0.5) {
                fieldBlocks.add(new Vec3d(x, posScale.getY(), z));
                fieldBlocks.add(new Vec3d(x, -negScale.getY(), z));
            }
        }
        // Left and right faces only (no front/back — that's what makes it a tube)
        for (double z = -negScale.getZ(); z <= posScale.getZ(); z += 0.5) {
            for (double y = -negScale.getY(); y <= posScale.getY(); y += 0.5) {
                fieldBlocks.add(new Vec3d(posScale.getX(), y, z));
                fieldBlocks.add(new Vec3d(-negScale.getX(), y, z));
            }
        }
        return fieldBlocks;
    }
}
