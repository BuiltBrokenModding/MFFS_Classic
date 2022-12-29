package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class DomeModuleItem extends ModuleItem {

    public DomeModuleItem() {
        super(ModItems.itemProperties());
    }

    @Override
    public void onCalculate(Projector projector, Set<BlockPos> fieldDefinition) {
        super.onCalculate(projector, fieldDefinition);

        int projectorYPos = ((BlockEntity) projector).getBlockPos().getY();
        fieldDefinition.removeIf(pos -> pos.getY() < projectorYPos);
    }
}
