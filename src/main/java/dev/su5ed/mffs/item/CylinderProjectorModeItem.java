package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class CylinderProjectorModeItem extends ProjectorModeItem {
    public static final int RADIUS_EXPANSION = 0;

    public CylinderProjectorModeItem() {
        super(ModItems.itemProperties());
    }

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
                    boolean fits = area <= radius * radius;
                    if (fits && (y == 0 || y == height - 1 || fits && area >= (radius - 1) * (radius - 1))) {
                        fieldBlocks.add(new Vec3(x, y, z));
                    }
                }
            }
        }

        return fieldBlocks;
    }

    @Override
    public Set<BlockPos> getInteriorPoints(Projector projector) {
        Set<BlockPos> fieldBlocks = new HashSet<>();

        BlockPos translation = projector.getTranslation();

        BlockPos posScale = projector.getPositiveScale();
        BlockPos negScale = projector.getNegativeScale();
        BlockPos projectorPos = projector.be().getBlockPos();

        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;
        int height = posScale.getY() + negScale.getY();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < height; y++) {
                    BlockPos position = new BlockPos(x, y, z);

                    if (isInField(projector, position.offset(projectorPos).offset(translation))) {
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

        int radius = (posScale.getX() + negScale.getX() + posScale.getZ() + negScale.getZ()) / 2;

        BlockPos projectorPos = ((BlockEntity) projector).getBlockPos().offset(projector.getTranslation());

        BlockPos relativePosition = position.subtract(projectorPos);
        BlockPos relativeRotated = ModUtil.rotateByAngle(relativePosition, -projector.getRotationYaw(), -projector.getRotationPitch(), 0);

        return relativeRotated.getX() * relativeRotated.getX() + relativeRotated.getZ() * relativeRotated.getZ() + RADIUS_EXPANSION <= radius * radius;
    }
}
