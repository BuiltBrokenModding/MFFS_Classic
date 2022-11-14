package dev.su5ed.mffs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockDropDelayedEvent extends DelayedEvent {
    private final Block block;
    private final Level level;
    private final BlockPos position;

    public BlockDropDelayedEvent(int ticks, Block block, Level world, BlockPos position) {
        super(ticks);
        this.block = block;
        this.level = world;
        this.position = position;
    }

    @Override
    protected void onEvent() {
		BlockState state = this.level.getBlockState(this.position); 
		if (state.is(this.block)) {
			Block.dropResources(state, level, this.position);
			this.level.removeBlock(this.position, false);
		}
    }
}
