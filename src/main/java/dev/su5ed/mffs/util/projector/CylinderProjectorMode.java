package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

import static dev.su5ed.mffs.util.projector.FieldSizeLimits.*;

public class CylinderProjectorMode implements ProjectorMode {
    // Heuristic multiplier to adjust radius to roughly match that of the Cube module at the same amount of Scale modules
    private static final double RADIUS_MULTIPLIER = 3.0;

    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int radius = clampHorizontal(projector, getRadius(posScale, negScale));
        int minY = -clampVertical(projector, negScale.getY());
        int maxY = clampVertical(projector, posScale.getY());

        for (float x = -radius; x <= radius; x += 1) {
            for (float z = -radius; z <= radius; z += 1) {
                for (float y = minY; y < maxY; y += 1) {
                    float area = x * x + z * z;
                    if (area <= radius * radius && (y == minY || y == maxY - 1 || area >= (radius - 1) * (radius - 1))) {
                        fieldBlocks.add(new Vec3(x, y, z));
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
        BlockPos projectorPos = projector.be().getBlockPos().offset(projector.getTranslation());

        int radius = clampHorizontal(projector, getRadius(posScale, negScale));
        int height = clampSize(posScale.getY() + negScale.getY(), projector.getMaxFieldHeight());

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < height; y++) {
                    Vec3 position = new Vec3(x, y, z);
                    if (isInField(projector, position.add(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ()), radius)) {
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
        int radius = getRadius(posScale, negScale);
        return isInField(projector, position, radius);
    }

    private int getRadius(BlockPos posScale, BlockPos negScale) {
        return (int) ((posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / RADIUS_MULTIPLIER);
    }

    private boolean isInField(Projector projector, Vec3 position, int radius) {
        BlockPos projectorPos = projector.be().getBlockPos().offset(projector.getTranslation());

        Vec3 relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3 relativeRotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);

        return relativeRotated.x() * relativeRotated.x() + relativeRotated.z() * relativeRotated.z() <= radius * radius;
    }
}
