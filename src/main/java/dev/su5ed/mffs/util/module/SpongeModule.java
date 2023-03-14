package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.IFluidBlock;

public class SpongeModule extends BaseModule {

    public SpongeModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void beforeProject(Projector projector) {
        if (projector.getTicks() % 60 == 0) {
            Level level = projector.be().getLevel();

            for (BlockPos pos : projector.getInteriorPoints()) {
                Block block = level.getBlockState(pos).getBlock();

                if (block instanceof LiquidBlock || block instanceof IFluidBlock) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}
