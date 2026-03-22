package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public final class PyramidProjectorMode implements ProjectorMode {
    PyramidProjectorMode() {}

    @Override
    public Set<Vec3d> getExteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();
        Vec3d translation = new Vec3d(0, -negScale.getY(), 0);

        if (xStretch <= 0 || yStretch <= 0 || zStretch <= 0) {
            fieldBlocks.add(translation);
            return fieldBlocks;
        }

        // Trace each of the 4 triangular faces by shooting ridge lines from base-edge
        // sample points (0.5-block spacing) to the apex.  The step along each line is
        // always <= 0.5 blocks in 3D, which guarantees no gaps survive the
        // rotation+round pass in ProjectorBlockEntity for any aspect ratio.
        double ax = 0, ay = yStretch, az = 0; // apex

        // ±Z faces: base edges run along X at z = ±zStretch
        for (double u = -xStretch; u <= xStretch; u += 0.5) {
            traceRidge(fieldBlocks, u, 0,  zStretch, ax, ay, az, translation);
            traceRidge(fieldBlocks, u, 0, -zStretch, ax, ay, az, translation);
        }
        // ±X faces: base edges run along Z at x = ±xStretch
        for (double v = -zStretch; v <= zStretch; v += 0.5) {
            traceRidge(fieldBlocks,  xStretch, 0, v, ax, ay, az, translation);
            traceRidge(fieldBlocks, -xStretch, 0, v, ax, ay, az, translation);
        }

        // Solid base at y = 0
        for (double x = -xStretch; x <= xStretch; x += 0.5) {
            for (double z = -zStretch; z <= zStretch; z += 0.5) {
                fieldBlocks.add(new Vec3d(x, 0, z).add(translation));
            }
        }

        return fieldBlocks;
    }

    /**
     * Traces a line from (bx,by,bz) to (ax,ay,az) in steps of <= 0.5 blocks,
     * adding each sample translated by {@code translation} to {@code out}.
     */
    private static void traceRidge(Set<Vec3d> out,
                                    double bx, double by, double bz,
                                    double ax, double ay, double az,
                                    Vec3d translation) {
        double dx = ax - bx, dy = ay - by, dz = az - bz;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 0.5) {
            out.add(new Vec3d(bx, by, bz).add(translation));
            return;
        }
        double tStep = 0.5 / len;
        for (double t = 0; t <= 1.0; t += tStep) {
            out.add(new Vec3d(bx + t * dx, by + t * dy, bz + t * dz).add(translation));
        }
        // Always include the apex end-point exactly
        out.add(new Vec3d(ax, ay, az).add(translation));
    }

    @Override
    public Set<Vec3d> getInteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();
        BlockPos projectorPos = projector.be().getPos();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();
        Vec3d translation = new Vec3d(0, -0.4, 0);

        for (float x = -xStretch; x <= xStretch; x++) {
            for (float z = -zStretch; z <= zStretch; z++) {
                for (float y = 0; y <= yStretch; y++) {
                    Vec3d position = new Vec3d(x, y, z).add(translation);

                    if (isInField(projector, position.add(new Vec3d(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ())))) {
                        fieldBlocks.add(position);
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, Vec3d position) {
        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();

        BlockPos projectorPos = projector.be().getPos()
            .add(projector.getTranslation())
            .add(0, -negScale.getY(), 0);

        Vec3d relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3d relativeRotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);

        // Replicate Vec3.atLowerCornerOf
        Vec3d min = new Vec3d(-negScale.getX(), -negScale.getY(), -negScale.getZ());
        Vec3d max = new Vec3d(posScale.getX(), posScale.getY(), posScale.getZ());

        return isIn(min, max, relativeRotated) && relativeRotated.y > 0
            && Math.max(Math.abs(relativeRotated.x) / xStretch, Math.abs(relativeRotated.z) / zStretch) < 1.0 - relativeRotated.y / yStretch;
    }

    private static boolean isIn(Vec3d min, Vec3d max, Vec3d vec) {
        return vec.x > min.x && vec.x < max.x
            && vec.y > min.y && vec.y < max.y
            && vec.z > min.z && vec.z < max.z;
    }
}
