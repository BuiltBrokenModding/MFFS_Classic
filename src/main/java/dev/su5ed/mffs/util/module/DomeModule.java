package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;

import java.util.Collection;

public class DomeModule extends ModuleBase {

    public DomeModule() {
        super(Category.MATRIX);
    }

    @Override
    public void onCalculate(Projector projector, Collection<BlockPos> fieldDefinition) {
        super.onCalculate(projector, fieldDefinition);

        int projectorYPos = projector.be().getBlockPos().getY();
        fieldDefinition.removeIf(pos -> pos.getY() < projectorYPos);
    }
}
