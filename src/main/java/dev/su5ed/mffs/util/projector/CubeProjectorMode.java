package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.HashSet;
import java.util.Set;

public class CubeProjectorMode implements ProjectorMode {
    CubeProjectorMode() {}

    @Override
    public Set<Vec3d> getExteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();
        Vec3i posScale = projector.getPositiveScale();
        Vec3i negScale = projector.getNegativeScale();
        // Top and bottom faces — step 0.5 so adjacent samples stay within 0.5*sqrt(3)<1
        // block apart after any rotation, preventing holes in the projected shell.
        for (double x = -negScale.getX(); x <= posScale.getX(); x += 0.5) {
            for (double z = -negScale.getZ(); z <= posScale.getZ(); z += 0.5) {
                fieldBlocks.add(new Vec3d(x, posScale.getY(), z));
                fieldBlocks.add(new Vec3d(x, -negScale.getY(), z));
            }
        }
        // Front and back faces
        for (double x = -negScale.getX(); x <= posScale.getX(); x += 0.5) {
            for (double y = -negScale.getY(); y <= posScale.getY(); y += 0.5) {
                fieldBlocks.add(new Vec3d(x, y, posScale.getZ()));
                fieldBlocks.add(new Vec3d(x, y, -negScale.getZ()));
            }
        }
        // Left and right faces
        for (double z = -negScale.getZ(); z <= posScale.getZ(); z += 0.5) {
            for (double y = -negScale.getY(); y <= posScale.getY(); y += 0.5) {
                fieldBlocks.add(new Vec3d(posScale.getX(), y, z));
                fieldBlocks.add(new Vec3d(-negScale.getX(), y, z));
            }
        }
        return fieldBlocks;
    }

    @Override
    public Set<Vec3d> getInteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();
        Vec3i posScale = projector.getPositiveScale();
        Vec3i negScale = projector.getNegativeScale();
        for (int x = -negScale.getX(); x <= posScale.getX(); x++) {
            for (int z = -negScale.getZ(); z <= posScale.getZ(); z++) {
                for (int y = -negScale.getY(); y <= posScale.getY(); y++) {
                    fieldBlocks.add(new Vec3d(x, y, z));
                }
            }
        }
        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, Vec3d position) {
        BlockPos projectorPos = projector.be().getPos().add(projector.getTranslation());
        Vec3d relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3d rotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), -projector.getRotationRoll());
        BlockPos negScale = projector.getNegativeScale();
        BlockPos posScale = projector.getPositiveScale();
        // Replicate AABB.encapsulatingFullBlocks(-negScale + (1,1,1), posScale).contains(rotated)
        // AABB.contains uses strict < for upper bounds (half-open interval)
        double minX = -negScale.getX() + 1;
        double minY = -negScale.getY() + 1;
        double minZ = -negScale.getZ() + 1;
        double maxX = posScale.getX() + 1;
        double maxY = posScale.getY() + 1;
        double maxZ = posScale.getZ() + 1;
        return rotated.x >= minX && rotated.x < maxX
            && rotated.y >= minY && rotated.y < maxY
            && rotated.z >= minZ && rotated.z < maxZ;
    }
}
