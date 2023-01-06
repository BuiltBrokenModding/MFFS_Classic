package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class PyramidProjectorModeItem extends ProjectorModeItem {

    public PyramidProjectorModeItem() {
        super(ModItems.itemProperties());
    }

    @Override
    public <T extends BlockEntity & Projector> Set<Vec3> getExteriorPoints(T projector) {
        Set<Vec3> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();
        Vec3 translation = new Vec3(0, -negScale.getY(), 0);

        int inverseThickness = 8;

        for (float y = 0; y <= yStretch; y += 1f) {
            for (float x = -xStretch; x <= xStretch; x += 1f) {
                for (float z = -zStretch; z <= zStretch; z += 1f) {
                    double yTest = y / yStretch * inverseThickness;
                    double xzPositivePlane = (1 - x / xStretch - z / zStretch) * inverseThickness;
                    double xzNegativePlane = (1 + x / xStretch - z / zStretch) * inverseThickness;

                    // Positive Positive Plane
                    if (x >= 0 && z >= 0 && Math.round(xzPositivePlane) == Math.round(yTest)) {
                        fieldBlocks.add(new Vec3(x, y, z).add(translation));
                        fieldBlocks.add(new Vec3(x, y, -z).add(translation));
                    }

                    // Negative Positive Plane
                    if (x <= 0 && z >= 0 && Math.round(xzNegativePlane) == Math.round(yTest)) {
                        fieldBlocks.add(new Vec3(x, y, -z).add(translation));
                        fieldBlocks.add(new Vec3(x, y, z).add(translation));
                    }

                    // Ground Level Plane
                    if (y == 0 && Math.abs(x) + Math.abs(z) < (xStretch + yStretch) / 2.0) {
                        fieldBlocks.add(new Vec3(x, y, z).add(translation));
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public <T extends BlockEntity & Projector> Set<BlockPos> getInteriorPoints(T projector) {
        Set<BlockPos> fieldBlocks = new HashSet<>();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();
        BlockPos translation = new BlockPos(0, -0.4, 0);

        for (float x = -xStretch; x <= xStretch; x++) {
            for (float z = -zStretch; z <= zStretch; z++) {
                for (float y = 0; y <= yStretch; y++) {
                    BlockPos position = new BlockPos(x, y, z).offset(translation);

                    if (isInField(projector, position.offset(projector.getBlockPos()))) {
                        fieldBlocks.add(position);
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public boolean isInField(Projector projector, BlockPos position) {
        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();

        int xStretch = posScale.getX() + negScale.getX();
        int yStretch = posScale.getY() + negScale.getY();
        int zStretch = posScale.getZ() + negScale.getZ();

        BlockPos projectorPos = ((BlockEntity) projector).getBlockPos()
            .offset(projector.getTranslation())
            .offset(0, -negScale.getY(), 0);

        BlockPos relativePosition = position.subtract(projectorPos);
        ModUtil.rotateByAngle(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);
        
        BlockPos min = negScale.multiply(-1);

        if (isIn(min, posScale, relativePosition) && relativePosition.getY() > 0) {
            if (1 - Math.abs(relativePosition.getX()) / xStretch - Math.abs(relativePosition.getZ()) / zStretch > relativePosition.getY() / yStretch) {
                return true;
            }
        }

        return false;
    }
    
    private static boolean isIn(Vec3i min, Vec3i max, Vec3i vec) {
        return vec.getX() > min.getX() && vec.getX() < max.getX() 
            && vec.getY() > min.getY() && vec.getY() < max.getY()
            && vec.getZ() > min.getZ() && vec.getZ() < max.getZ();
    }
}
