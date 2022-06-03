package dev.su5ed.mffs.block;

import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ProjectorBlock extends Block implements EntityBlock {
    public static final Property<Boolean> ACTIVE = BooleanProperty.create("active");
    
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 12, 16),
        Block.box(3, 12, 5, 13, 14, 11),
        Block.box(5, 12, 3, 11, 14, 13),
        Block.box(4, 12, 4, 12, 14, 12)
    );

    public ProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext)
            .setValue(ACTIVE, false);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return getBlockEntity(pLevel, pPos)
            .map(be -> be.use(pState, pLevel, pPos, pPlayer, pHand, pHit))
            .orElseGet(() -> super.use(pState, pLevel, pPos, pPlayer, pHand, pHit));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ProjectorBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, stt, te) -> {
            if (lvl.isClientSide() && te instanceof ProjectorBlockEntity machine) {
                machine.tickClient();
            }
        };
    }

    private Optional<ProjectorBlockEntity> getBlockEntity(BlockGetter world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof ProjectorBlockEntity projectorBe
            ? Optional.of(projectorBe)
            : Optional.empty();
    }
}
