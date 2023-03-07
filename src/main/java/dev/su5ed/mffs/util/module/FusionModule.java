package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.FrequencyBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.FrequencyGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Set;

public class FusionModule extends BaseModule {

    public FusionModule() {
        super(1);
    }

    @Override
    public boolean beforeProject(Projector projector, Collection<? extends BlockPos> field) {
        int frequency = projector.be().getCapability(ModCapabilities.FORTRON)
            .map(FrequencyBlock::getFrequency)
            .orElseThrow();
        Set<FortronStorage> machines = FrequencyGrid.instance().get(frequency);

        for (FortronStorage storage : machines) {
            storage.getOwner().getCapability(ModCapabilities.PROJECTOR)
                .filter(compareProjector ->  compareProjector != projector
                    && compareProjector.be().getLevel() == projector.be().getLevel()
                    && compareProjector.isActive() && compareProjector.getMode().isPresent())
                .ifPresent(compareProjector -> field.removeIf(pos -> compareProjector.getMode().orElseThrow().isInField(compareProjector, Vec3.atLowerCornerOf(pos))));
        }
        return super.beforeProject(projector, field);
    }
}
