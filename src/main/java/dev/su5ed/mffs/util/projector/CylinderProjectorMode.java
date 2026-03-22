package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public class CylinderProjectorMode implements ProjectorMode {
    public static final int RADIUS_EXPANSION = 0;

    CylinderProjectorMode() {}

    @Override
    public Set<Vec3d> getExteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;
        int negY = negScale.getY();
        int posY = posScale.getY();

        // Step 0.5 prevents holes in the cylindrical shell after any rotation
        for (double x = -radius; x <= radius; x += 0.5) {
            for (double z = -radius; z <= radius; z += 0.5) {
                for (double y = -negY; y <= posY; y += 0.5) {
                    double area = x * x + z * z + RADIUS_EXPANSION;
                    if (area <= radius * radius && (y == -negY || y == posY || area >= (radius - 1) * (radius - 1))) {
                        fieldBlocks.add(new Vec3d(x, y, z));
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public Set<Vec3d> getInteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();
        BlockPos projectorPos = projector.be().getPos().add(projector.getTranslation());

        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;
        int negY = negScale.getY();
        int posY = posScale.getY();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -negY; y <= posY; y++) {
                    Vec3d position = new Vec3d(x, y, z);
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
        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;
        BlockPos projectorPos = projector.be().getPos().add(projector.getTranslation());

        Vec3d relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3d relativeRotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);

        return relativeRotated.x * relativeRotated.x + relativeRotated.z * relativeRotated.z + RADIUS_EXPANSION <= radius * radius;
    }
}
