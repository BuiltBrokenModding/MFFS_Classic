package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class CubeProjectorModeItem extends ProjectorModeItem {

    public CubeProjectorModeItem() {
        super(ModItems.itemProperties());
    }

    @Override
    public <T extends BlockEntity & Projector> Set<Vec3> getExteriorPoints(T projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();
        Vec3i posScale = projector.getPositiveScale();
        Vec3i negScale = projector.getNegativeScale();
        for (float x = -negScale.getX(); x <= posScale.getX(); x += 0.5f) {
            for (float z = -negScale.getZ(); z <= posScale.getZ(); z += 0.5f) {
                for (float y = -negScale.getY(); y <= posScale.getY(); y += 0.5f) {
                    if (y == -negScale.getY() || y == posScale.getY() || x == -negScale.getX() || x == posScale.getX() || z == -negScale.getZ() || z == posScale.getZ()) {
                        fieldBlocks.add(new Vec3(Math.round(x), Math.round(y), Math.round(z)));
                    }
                }
            }
        }
        return fieldBlocks;
    }

    @Override
    public <T extends BlockEntity & Projector> Set<BlockPos> getInteriorPoints(T projector) {
        Set<BlockPos> fieldBlocks = new HashSet<>();
        Vec3i posScale = projector.getPositiveScale();
        Vec3i negScale = projector.getNegativeScale();
        for (int x = -negScale.getX(); x <= posScale.getX(); x++) {
            for (int z = -negScale.getZ(); z <= posScale.getZ(); z++) {
                for (int y = -negScale.getY(); y <= posScale.getY(); y++) {
                    fieldBlocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, BlockPos position) {
        BlockPos projectorPos = ((BlockEntity) projector).getBlockPos().offset(projector.getTranslation());
        BlockPos relativePosition = position.subtract(projectorPos);
        BlockPos rotated = ModUtil.rotateByAngle(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), -projector.getRotationRoll());
        AABB region = new AABB(projector.getNegativeScale().multiply(-1).offset(1, 1, 1), projector.getPositiveScale());
        return region.contains(rotated.getX(), rotated.getY(), rotated.getZ());
    }
}