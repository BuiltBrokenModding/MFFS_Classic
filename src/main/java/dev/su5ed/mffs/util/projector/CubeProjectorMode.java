package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class CubeProjectorMode implements ProjectorMode {
    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();
        Vec3i posScale = projector.getPositiveScale();
        Vec3i negScale = projector.getNegativeScale();
        for (int x = -negScale.getX(); x <= posScale.getX(); x++) {
            for (int z = -negScale.getZ(); z <= posScale.getZ(); z++) {
                fieldBlocks.add(new Vec3(x, posScale.getY(), z));
                fieldBlocks.add(new Vec3(x, -negScale.getY(), z));
            }
        }
        for (int x = -negScale.getX(); x <= posScale.getX(); x++) {
            for (int y = -negScale.getY(); y <= posScale.getY(); y++) {
                fieldBlocks.add(new Vec3(x, y, posScale.getZ()));
                fieldBlocks.add(new Vec3(x, y, -negScale.getZ()));
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

    @Override
    public Set<Vec3> getInteriorPoints(Projector projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();
        Vec3i posScale = projector.getPositiveScale();
        Vec3i negScale = projector.getNegativeScale();
        for (int x = -negScale.getX(); x <= posScale.getX(); x++) {
            for (int z = -negScale.getZ(); z <= posScale.getZ(); z++) {
                for (int y = -negScale.getY(); y <= posScale.getY(); y++) {
                    fieldBlocks.add(new Vec3(x, y, z));
                }
            }
        }
        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, Vec3 position) {
        BlockPos projectorPos = projector.be().getBlockPos().offset(projector.getTranslation());
        Vec3 relativePosition = position.subtract(projectorPos.getX(), projectorPos.getY(), projectorPos.getZ());
        Vec3 rotated = ModUtil.rotateByAngleExact(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), -projector.getRotationRoll());
        AABB region = new AABB(projector.getNegativeScale().multiply(-1).offset(1, 1, 1), projector.getPositiveScale());
        return region.contains(rotated.x(), rotated.y(), rotated.z());
    }
}
