package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class TubeProjectorMode extends CubeProjectorMode {
    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();
        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();
        for (int x = -negScale.getX(); x <= posScale.getX(); x++) {
            for (int z = -negScale.getZ(); z <= posScale.getZ(); z++) {
                fieldBlocks.add(new Vec3(x, posScale.getY(), z));
                fieldBlocks.add(new Vec3(x, -negScale.getY(), z));
            }
        }
        for (int z = -negScale.getZ(); z <= posScale.getZ(); z++) {
            for (int y = -negScale.getY(); y <= posScale.getY(); y++) {
                fieldBlocks.add(new Vec3(posScale.getX(), y, z));
                fieldBlocks.add(new Vec3(-negScale.getX(), y, z));
            }
        }
        return fieldBlocks;
    }
}
