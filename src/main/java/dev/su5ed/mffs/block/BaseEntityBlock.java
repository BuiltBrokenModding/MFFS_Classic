package dev.su5ed.mffs.block;

import dev.su5ed.mffs.blockentity.BaseBlockEntity;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class BaseEntityBlock extends Block implements EntityBlock {
    public static final Property<Boolean> ACTIVE = BooleanProperty.create("active");

    private final Supplier<BlockEntityType<? extends BaseBlockEntity>> provider;

    public BaseEntityBlock(Properties properties, Supplier<BlockEntityType<? extends BaseBlockEntity>> provider) {
        super(properties);

        this.provider = provider;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return getBlockEntity(level, pos)
            .map(be -> be.use(player, hand, hit))
            .orElseGet(() -> super.use(state, level, pos, player, hand, hit));
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.provider.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (lvl, pos, stt, te) -> {
            if (te instanceof BaseBlockEntity machine) {
                if (lvl.isClientSide()) machine.tickClient();
                else machine.tickServer();
            }
        };
    }

    private Optional<? extends BaseBlockEntity> getBlockEntity(BlockGetter world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof BaseBlockEntity machineBe
            ? Optional.of(machineBe)
            : Optional.empty();
    }
}
