package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.setup.ModModules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class SphereProjectorMode implements ProjectorMode {
    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();
        int radius = projector.getModuleCount(ModModules.SCALE);
        double rSq = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distSq = x*x + y*y + z*z;

                    if (distSq <= rSq && distSq >= (radius - 1)*(radius - 1)) {
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
        BlockPos projectorPos = projector.be().getBlockPos().offset(projector.getTranslation());
        int radius = projector.getModuleCount(ModModules.SCALE);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    Vec3 position = new Vec3(x, y, z);
                    if (isInField(projector, position.add(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ()), 0.1)) {
                        fieldBlocks.add(position);
                    }
                }
            }
        }
        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, Vec3 position) {
        return isInField(projector, position, -0.5);
    }

    private boolean isInField(Projector projector, Vec3 position, double tolerance) {
        BlockPos projectorPos = projector.be().getBlockPos();
        int radius = projector.getModuleCount(ModModules.SCALE);
        return projectorPos.offset(projector.getTranslation()).closerThan(BlockPos.containing(position), radius + tolerance);
    }
}
