package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public final class PyramidProjectorMode implements ProjectorMode {
    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();
        Vec3 translation = new Vec3(0, -negScale.getY(), 0);

        int inverseThickness = 8;

        for (float y = 0; y <= yStretch; y += 1f) {
            for (float x = -xStretch; x <= xStretch; x += 1f) {
                for (float z = -zStretch; z <= zStretch; z += 1f) {
                    double yTest = y / yStretch * inverseThickness;
                    double xzPositivePlane = (1 - x / xStretch - z / zStretch) * inverseThickness;
                    double xzNegativePlane = (1 + x / xStretch - z / zStretch) * inverseThickness;

                    // Positive Positive Plane
                    if (x >= 0 && z >= 0 && Math.round(xzPositivePlane) == Math.round(yTest)) {
                        fieldBlocks.add(new Vec3(x, y, z).add(translation));
                        fieldBlocks.add(new Vec3(x, y, -z).add(translation));
                    }

                    // Negative Positive Plane
                    if (x <= 0 && z >= 0 && Math.round(xzNegativePlane) == Math.round(yTest)) {
                        fieldBlocks.add(new Vec3(x, y, -z).add(translation));
                        fieldBlocks.add(new Vec3(x, y, z).add(translation));
                    }

                    // Ground Level Plane
                    if (y == 0 && Math.abs(x) + Math.abs(z) < (xStretch + yStretch) / 2.0) {
                        fieldBlocks.add(new Vec3(x, y, z).add(translation));
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public Set<Vec3> getInteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();
        BlockPos projectorPos = projector.be().getBlockPos();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();
        Vec3 translation = new Vec3(0, -0.4, 0);

        for (float x = -xStretch; x <= xStretch; x++) {
            for (float z = -zStretch; z <= zStretch; z++) {
                for (float y = 0; y <= yStretch; y++) {
                    Vec3 position = new Vec3(x, y, z).add(translation);

                    if (isInField(projector, position.add(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ()))) {
                        fieldBlocks.add(position);
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, Vec3 position) {
        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();

        BlockPos projectorPos = projector.be().getBlockPos()
            .offset(projector.getTranslation())
            .offset(0, -negScale.getY(), 0);

        Vec3 relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3 relativeRotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);

        Vec3 min = Vec3.atLowerCornerOf(negScale.multiply(-1));

        return isIn(min, Vec3.atLowerCornerOf(posScale), relativeRotated) && relativeRotated.y() > 0
            && 1 - Math.abs(relativeRotated.x()) / xStretch - Math.abs(relativeRotated.z()) / zStretch > relativeRotated.y() / yStretch;
    }

    private static boolean isIn(Vec3 min, Vec3 max, Vec3 vec) {
        return vec.x() > min.x() && vec.x() < max.x()
            && vec.y() > min.y() && vec.y() < max.y()
            && vec.z() > min.z() && vec.z() < max.z();
    }
}
