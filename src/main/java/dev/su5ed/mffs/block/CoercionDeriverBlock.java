package dev.su5ed.mffs.block;

import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CoercionDeriverBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 2.65, 16),
        Block.box(0, 0, 0, 4, 10.65, 4),
        Block.box(12, 0, 12, 16, 10.65, 16),
        Block.box(0, 0, 12, 4, 10.65, 16),
        Block.box(12, 0, 0, 16, 10.65, 4),
        Block.box(0, 0, 4, 4, 6.65, 12),
        Block.box(12, 0, 4, 16, 6.65, 12),
        Block.box(4, 4, 4, 12, 12, 12)
    );

    public CoercionDeriverBlock(Properties properties) {
        super(properties, ModObjects.COERCION_DERIVER_BLOCK_ENTITY);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }
}
