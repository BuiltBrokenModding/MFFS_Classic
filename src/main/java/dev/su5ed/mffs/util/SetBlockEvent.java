package dev.su5ed.mffs.util;

// 1.12.2 Backport: extends Forge Event; LevelAccessor → World; BlockState → IBlockState.

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SetBlockEvent extends Event {
    private final World world;
    private final BlockPos pos;
    private final IBlockState state;

    public SetBlockEvent(World world, BlockPos pos, IBlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public IBlockState getState() {
        return this.state;
    }
}
