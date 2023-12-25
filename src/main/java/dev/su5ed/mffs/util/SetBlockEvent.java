package dev.su5ed.mffs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.LevelEvent;

public class SetBlockEvent extends LevelEvent {
    private final BlockPos pos;
    private final BlockState state;

    public SetBlockEvent(LevelAccessor level, BlockPos pos, BlockState state) {
        super(level);
        this.pos = pos;
        this.state = state;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }
}
