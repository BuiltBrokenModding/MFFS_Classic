package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.ModObjects;
import dev.su5ed.mffs.block.ProjectorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ProjectorBlockEntity extends BlockEntity {
    private long tickCounter;
    private long animation;
    private boolean active;

    public ProjectorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        this.active = !this.active;
        this.level.setBlock(this.worldPosition, pState.setValue(ProjectorBlock.ACTIVE, this.active), Block.UPDATE_ALL);
        return InteractionResult.SUCCESS;
    }

    public long getTicks() {
        return this.tickCounter;
    }
    
    public long getAnimation() {
        return this.animation;
    }
    
    public boolean isActive() {
        return this.active;
    }

    public void tickClient() {
        this.tickCounter++;
        
        if (this.active) this.animation++;
    }
}
