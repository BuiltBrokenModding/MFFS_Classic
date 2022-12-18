package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.FrequencyBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.fortron.FrequencyGrid;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class FusionModule extends ModuleItem {

    public FusionModule() {
        super(ModItems.itemProperties().stacksTo(1), 1);
    }

    @Override
    public boolean onProject(Projector projector, Set<BlockPos> field) {
        int frequency = ((BlockEntity) projector).getCapability(ModCapabilities.FORTRON)
            .map(FrequencyBlock::getFrequency)
            .orElseThrow();
        Set<FortronStorage> machines = FrequencyGrid.instance().get(frequency);

        for (FortronStorage storage : machines) {
            if (storage.getOwner() instanceof Projector compareProjector && compareProjector != projector
                && ((BlockEntity) compareProjector).getLevel() == ((BlockEntity) projector).getLevel()
                && compareProjector.isActive() && compareProjector.getMode() != null) {
                field.removeIf(pos -> compareProjector.getMode().isInField(compareProjector, pos));
            }
        }
        return super.onProject(projector, field);
    }
}
