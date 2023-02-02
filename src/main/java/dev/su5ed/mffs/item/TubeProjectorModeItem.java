package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class TubeProjectorModeItem extends CubeProjectorModeItem {

    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();
        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();
        for (float x = -negScale.getX(); x <= posScale.getX(); x += 0.5f) {
            for (float z = -negScale.getZ(); z <= posScale.getZ(); z += 0.5f) {
                for (float y = -negScale.getY(); y <= posScale.getY(); y += 0.5f) {
                    if (y == -negScale.getY() || y == posScale.getY() || x == -negScale.getX() || x == posScale.getX()) {
                        fieldBlocks.add(new Vec3(x, y, z));
                    }
                }
            }
        }
        return fieldBlocks;
    }
}
