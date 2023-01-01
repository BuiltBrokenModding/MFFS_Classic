package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.Set;

public class SpongeModuleItem extends ModuleItem {

    public SpongeModuleItem() {
        super(ModItems.itemProperties().stacksTo(1));
    }

    @Override
    public boolean beforeProject(Projector projector, Set<BlockPos> field) {
        if (projector.getTicks() % 60 == 0) {
            Level level = ((BlockEntity) projector).getLevel();

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
