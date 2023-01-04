package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Set;

public class SphereProjectorModeItem extends ProjectorModeItem {

    public SphereProjectorModeItem() {
        super(ModItems.itemProperties());
    }

    @Override
    public <T extends BlockEntity & Projector> Set<BlockPos> getExteriorPoints(T projector) {
        Set<BlockPos> fieldBlocks = new HashSet<>();
        int radius = projector.getModuleCount(ModItems.SCALE_MODULE.get());
        int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / radius / 2));
        for (int phi_n = 0; phi_n < 2 * steps; phi_n++) {
            for (int theta_n = 0; theta_n < steps; theta_n++) {
                double phi = Math.PI * 2 / steps * phi_n;
                double theta = Math.PI / steps * theta_n;

                double x = Math.sin(theta) * Math.cos(phi) * radius;
                double y = Math.cos(theta) * radius;
                double z = Math.sin(theta) * Math.sin(phi) * radius;
                BlockPos point = new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z));
                fieldBlocks.add(point);
            }
        }
        return fieldBlocks;
    }

    @Override
    public Set<BlockPos> getInteriorPoints(Projector projector) {
        Set<BlockPos> fieldBlocks = new HashSet<>();
        BlockPos projectorPos = ((BlockEntity) projector).getBlockPos();
        BlockPos translation = projector.getTranslation();
        int radius = projector.getModuleCount(ModItems.SCALE_MODULE.get());
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
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
        BlockPos projectorPos = ((BlockEntity) projector).getBlockPos();
        int radius = projector.getModuleCount(ModItems.SCALE_MODULE.get());
        return projectorPos.offset(projector.getTranslation()).closerThan(position, radius);
    }
}