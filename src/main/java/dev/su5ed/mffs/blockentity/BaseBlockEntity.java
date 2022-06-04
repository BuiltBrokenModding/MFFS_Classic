package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.block.BaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class BaseBlockEntity extends BlockEntity {
    private boolean active;
    private long tickCounter;

    protected BaseBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public long getTicks() {
        return this.tickCounter;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;

        BlockState state = getBlockState().setValue(BaseEntityBlock.ACTIVE, this.active);
        this.level.setBlock(this.worldPosition, state, Block.UPDATE_ALL);
    }

    public InteractionResult use(Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return InteractionResult.PASS;
    }

    public void tickClient() {
        ++this.tickCounter;
    }

    public void tickServer() {
        ++this.tickCounter;
    }
}
