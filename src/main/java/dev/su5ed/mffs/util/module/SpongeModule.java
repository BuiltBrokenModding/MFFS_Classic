package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SpongeModule extends BaseModule {
    private final ConcurrentLinkedQueue<BlockPos> removingBlocks = new ConcurrentLinkedQueue<>();

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
            int count = 0;
            for (int i = 0; i < interiorPoints.size() && count < maxRemove; i++) {
                BlockPos pos = interiorPoints.get(i);
                World world = projector.be().getWorld();
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                // 1.12.2: check for vanilla liquid blocks and Forge fluid blocks
                if (block instanceof BlockLiquid || block instanceof IFluidBlock) {
                    this.removingBlocks.add(pos);
                    // Count source blocks
                    if (block instanceof BlockLiquid && state.getValue(BlockLiquid.LEVEL) == 0) {
                        count++;
                    }
                }
            }
        }
    }

    @Override
    public void beforeProject(Projector projector) {
        World world = projector.be().getWorld();
        BlockPos pos;
        while ((pos = this.removingBlocks.poll()) != null) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
