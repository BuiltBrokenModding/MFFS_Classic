package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.HashSet;
import java.util.Set;

public class SphereProjectorMode implements ProjectorMode {
    SphereProjectorMode() {}

    private static int getRadius(Projector projector) {
        Vec3i posScale = projector.getPositiveScale();
        Vec3i negScale = projector.getNegativeScale();
        int axisX = posScale.getX() + negScale.getX();
        int axisY = posScale.getY() + negScale.getY();
        int axisZ = posScale.getZ() + negScale.getZ();
        return Math.max(Math.max(axisX, axisY), axisZ) / 2;
    }

    @Override
    public Set<Vec3d> getExteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();
        int radius = getRadius(projector);
        int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / radius / 2));
        for (int phi_n = 0; phi_n < 2 * steps; phi_n++) {
            for (int theta_n = 0; theta_n < steps; theta_n++) {
                double phi = Math.PI * 2 / steps * phi_n;
                double theta = Math.PI / steps * theta_n;

                double x = Math.sin(theta) * Math.cos(phi) * radius;
                double y = Math.cos(theta) * radius;
                double z = Math.sin(theta) * Math.sin(phi) * radius;
                fieldBlocks.add(new Vec3d(x, y, z));
            }
        }
        return fieldBlocks;
    }

    @Override
    public Set<Vec3d> getInteriorPoints(Projector projector) {
        Set<Vec3d> fieldBlocks = new HashSet<>();
        BlockPos projectorPos = projector.be().getPos().add(projector.getTranslation());
        int radius = getRadius(projector);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    Vec3d position = new Vec3d(x, y, z);
                    if (isInField(projector, position.add(new Vec3d(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ())), 0.1)) {
                        fieldBlocks.add(position);
                    }
                }
            }
        }
        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, Vec3d position) {
        return isInField(projector, position, -0.5);
    }

    private boolean isInField(Projector projector, Vec3d position, double tolerance) {
        BlockPos projectorPos = projector.be().getPos();
        int radius = getRadius(projector);
        BlockPos center = projectorPos.add(projector.getTranslation());
        BlockPos target = new BlockPos(position.x, position.y, position.z);
        double distSq = center.distanceSq(target);
        double limit = radius + tolerance;
        return distSq < limit * limit;
    }
}
