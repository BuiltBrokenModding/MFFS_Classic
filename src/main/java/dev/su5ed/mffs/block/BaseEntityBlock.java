package dev.su5ed.mffs.block;

import dev.su5ed.mffs.blockentity.BaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.su5ed.mffs.MFFSMod.location;

public class BaseEntityBlock extends Block implements EntityBlock {
    public static final ResourceLocation CONTENT_KEY = location("content");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private final Supplier<? extends BlockEntityType<? extends BaseBlockEntity>> provider;

    public BaseEntityBlock(Properties properties, Supplier<? extends BlockEntityType<? extends BaseBlockEntity>> provider) {
        super(properties);

        this.provider = provider;
        registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return getBlockEntity(level, pos)
            .map(be -> be.useWithoutItem(state, level, pos, player, hit))
            .orElseGet(() -> super.useWithoutItem(state, level, pos, player, hit));
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
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof BaseBlockEntity base) {
            params.withDynamicDrop(CONTENT_KEY, consumer -> {
                List<ItemStack> drops = new ArrayList<>();
                base.provideAdditionalDrops(drops);
                drops.forEach(consumer);
            });
        }
        return super.getDrops(state, params);
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
                if (lvl.isClientSide()) {
                    machine.tickClient();
                } else {
                    machine.tickServer();
                }
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
