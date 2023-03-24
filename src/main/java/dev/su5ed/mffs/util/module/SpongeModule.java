package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.module.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SpongeModule extends BaseModule {
    private final List<BlockPos> removingBlocks = new ArrayList<>();

    public SpongeModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void beforeSelect(Projector projector, Collection<? extends TargetPosPair> field) {
        super.beforeSelect(projector, field);

        if (projector.getTicks() % 60 == 0) {
            List<BlockPos> interiorPoints = new ArrayList<>(projector.getInteriorPoints());
            Collections.shuffle(interiorPoints);
            int maxRemove = 50 + projector.getProjectionSpeed() * 20;
            for (int i = 0, count = 0; i < interiorPoints.size() && count < maxRemove; i++) {
                BlockPos pos = interiorPoints.get(i);
                BlockState state = projector.getCachedBlockState(pos);
                Block block = state.getBlock();
                FluidState fluidState = state.getFluidState();
                if ((block instanceof LiquidBlock || block instanceof IFluidBlock) && !fluidState.isEmpty()) {
                    this.removingBlocks.add(pos);
                    if (fluidState.isSource()) {
                        count++;
                    }
                }
            }
        }
    }

    @Override
    public void beforeProject(Projector projector) {
        Level level = projector.be().getLevel();
        for (BlockPos pos : this.removingBlocks) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        this.removingBlocks.clear();
    }
}
