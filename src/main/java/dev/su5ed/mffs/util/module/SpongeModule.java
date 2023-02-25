package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.Collection;

public class SpongeModule extends ModuleBase {

    public SpongeModule() {
        super(1);
    }

    @Override
    public boolean beforeProject(Projector projector, Collection<? extends BlockPos> field) {
        if (projector.getTicks() % 60 == 0) {
            Level level = projector.be().getLevel();

            for (BlockPos pos : projector.getInteriorPoints()) {
                Block block = level.getBlockState(pos).getBlock();

                if (block instanceof LiquidBlock || block instanceof IFluidBlock) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
        return super.beforeProject(projector, field);
    }
}