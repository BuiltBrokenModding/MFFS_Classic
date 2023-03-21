package dev.su5ed.mffs.block;

import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BiometricIdentifierBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 3, 16),
        Block.box(2, 3, 2, 14, 5, 14),
        Block.box(0, 5, 0, 16, 8, 16),
        Block.box(0, 5, 12, 4, 13, 16),
        Block.box(12, 5, 12, 16, 13, 16)
    );

    private final Map<BlockState, VoxelShape> shapesCache;

    public BiometricIdentifierBlock(Properties properties) {
        super(properties, ModObjects.BIOMETRIC_IDENTIFIER_BLOCK_ENTITY);

        this.shapesCache = getShapeForEachState(state -> ModUtil.rotateShape(Direction.NORTH, state.getValue(BlockStateProperties.HORIZONTAL_FACING), SHAPE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
            .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapesCache.get(state);
    }
}
