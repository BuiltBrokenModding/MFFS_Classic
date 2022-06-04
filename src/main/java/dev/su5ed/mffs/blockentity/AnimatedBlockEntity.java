package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AnimatedBlockEntity extends BaseBlockEntity {
    private long animation;

    public AnimatedBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    protected AnimatedBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public long getAnimation() {
        return this.animation;
    }

    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        setActive(!isActive());
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tickClient() {
        super.tickClient();

        if (isActive()) this.animation++;
    }
}
