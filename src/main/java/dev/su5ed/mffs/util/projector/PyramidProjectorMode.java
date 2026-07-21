package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

import static dev.su5ed.mffs.util.projector.FieldSizeLimits.clampHorizontal;
import static dev.su5ed.mffs.util.projector.FieldSizeLimits.clampSize;

public final class PyramidProjectorMode implements ProjectorMode {
    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();

        Bounds bounds = getBounds(projector);
        if (bounds.isEmpty()) return fieldBlocks;

        Vec3 translation = new Vec3(0, -projector.getNegativeScale().getY(), 0);

        for (int y = 0; y <= bounds.height(); y++) {
            for (int x = -bounds.xRadius(); x <= bounds.xRadius(); x++) {
                for (int z = -bounds.zRadius(); z <= bounds.zRadius(); z++) {
                    if (bounds.contains(x, y, z) && bounds.isSurface(x, y, z)) {
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

        Bounds bounds = getBounds(projector);
        if (bounds.isEmpty()) {
            return fieldBlocks;
        }
        Vec3 translation = new Vec3(0, -projector.getNegativeScale().getY(), 0);

        for (int y = 0; y <= bounds.height(); y++) {
            for (int x = -bounds.xRadius(); x <= bounds.xRadius(); x++) {
                for (int z = -bounds.zRadius(); z <= bounds.zRadius(); z++) {
                    if (bounds.contains(x, y, z)) {
                        fieldBlocks.add(new Vec3(x, y, z).add(translation));
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, Vec3 position) {
        Bounds bounds = getBounds(projector);
        if (bounds.isEmpty()) return false;

        BlockPos projectorPos = projector.be().getBlockPos()
            .offset(projector.getTranslation())
            .offset(0, -projector.getNegativeScale().getY(), 0);

        Vec3 relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3 relativeRotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);

        return relativeRotated.y() >= 0 && relativeRotated.y() <= bounds.height()
            && Math.abs(relativeRotated.x()) / bounds.xRadius() + Math.abs(relativeRotated.z()) / bounds.zRadius() <= 1 - relativeRotated.y() / bounds.height();
    }

    private static Bounds getBounds(Projector projector) {
        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xRadius = clampHorizontal(projector, posScale.getX() + negScale.getX());
        int zRadius = clampHorizontal(projector, posScale.getZ() + negScale.getZ());
        int height = clampSize(posScale.getY() + negScale.getY(), projector.getMaxFieldHeight());

        return new Bounds(xRadius, height, zRadius);
    }

    private record Bounds(int xRadius, int height, int zRadius) {
        public boolean isEmpty() {
            return this.xRadius <= 0 || this.height <= 0 || this.zRadius <= 0;
        }

        public boolean isSurface(int x, int y, int z) {
            return !contains(x + 1, y, z) || !contains(x - 1, y, z)
                || !contains(x, y + 1, z) || !contains(x, y - 1, z)
                || !contains(x, y, z + 1) || !contains(x, y, z - 1);
        }

        public boolean contains(int x, int y, int z) {
            return y >= 0 && y <= this.height
                && (Math.abs(x) * this.zRadius + Math.abs(z) * this.xRadius) * this.height <= this.xRadius * this.zRadius * (this.height - y);
        }
    }
}
