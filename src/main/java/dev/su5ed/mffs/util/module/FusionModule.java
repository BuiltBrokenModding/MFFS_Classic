package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.FrequencyBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.FrequencyGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FusionModule extends BaseModule {
    private final List<BlockPos> removingBlocks = new ArrayList<>();

    public FusionModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void beforeSelect(Projector projector, Collection<? extends TargetPosPair> field) {
        int frequency = projector.be().getCapability(ModCapabilities.FORTRON)
            .map(FrequencyBlock::getFrequency)
            .orElseThrow();
        Level level = projector.be().getLevel();
        for (FortronStorage storage : FrequencyGrid.instance(level.isClientSide).get(frequency)) {
            storage.getOwner().getCapability(ModCapabilities.PROJECTOR)
                .filter(compareProjector -> compareProjector != projector
                    && compareProjector.be().getLevel() == level
                    && compareProjector.isActive() && compareProjector.getMode().isPresent())
                .ifPresent(compareProjector -> {
                    for (Iterator<? extends TargetPosPair> it = field.iterator(); it.hasNext(); ) {
                        BlockPos pos = it.next().pos();
                        if (compareProjector.getMode().orElseThrow().isInField(compareProjector, Vec3.atLowerCornerOf(pos))) {
                            this.removingBlocks.add(pos);
                            it.remove();
                        }
                    }
                });
        }
    }

    @Override
    public void beforeProject(Projector projector) {
        Level level = projector.be().getLevel();
        for (BlockPos pos : this.removingBlocks) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlocks.FORCE_FIELD.get())) {
                level.removeBlock(pos, false);
            }
        }
        this.removingBlocks.clear();
    }
}
