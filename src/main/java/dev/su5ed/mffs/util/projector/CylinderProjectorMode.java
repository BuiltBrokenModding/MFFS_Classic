package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class CylinderProjectorMode implements ProjectorMode {
    public static final int RADIUS_EXPANSION = 0;
    
    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;
        int height = posScale.getY() + negScale.getY();

        for (float x = -radius; x <= radius; x += 1) {
            for (float z = -radius; z <= radius; z += 1) {
                for (float y = 0; y < height; y += 1) {
                    float area = x * x + z * z + RADIUS_EXPANSION;
                    if (area <= radius * radius && (y == 0 || y == height - 1 || area >= (radius - 1) * (radius - 1))) {
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

        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;
        int height = posScale.getY() + negScale.getY();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < height; y++) {
                    Vec3 position = new Vec3(x, y, z);
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
        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;
        BlockPos projectorPos = ((BlockEntity) projector).getBlockPos().offset(projector.getTranslation());

        Vec3 relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3 relativeRotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);

        return relativeRotated.x() * relativeRotated.x() + relativeRotated.z() * relativeRotated.z() + RADIUS_EXPANSION <= radius * radius;
    }
}
