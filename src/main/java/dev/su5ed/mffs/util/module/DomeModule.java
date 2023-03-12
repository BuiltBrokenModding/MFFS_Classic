package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class DomeModule extends BaseModule {

    public DomeModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void onCalculate(Projector projector, Collection<BlockPos> fieldDefinition) {
        super.onCalculate(projector, fieldDefinition);

        int projectorYPos = projector.be().getBlockPos().getY();
        fieldDefinition.removeIf(pos -> pos.getY() < projectorYPos);
    }
}
