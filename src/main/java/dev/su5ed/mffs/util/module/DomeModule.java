package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class DomeModule extends ModuleBase {
    @Override
    public void onCalculate(Projector projector, Set<BlockPos> fieldDefinition) {
        super.onCalculate(projector, fieldDefinition);

        int projectorYPos = ((BlockEntity) projector).getBlockPos().getY();
        fieldDefinition.removeIf(pos -> pos.getY() < projectorYPos);
    }
}
