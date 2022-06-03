package dev.su5ed.mffs.block;

import dev.su5ed.mffs.blockentity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MachineBlock extends Block implements EntityBlock {

    public MachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return getBlockEntity(pLevel, pPos)
            .map(be -> be.use(pState, pLevel, pPos, pPlayer, pHand, pHit))
            .orElseGet(() -> super.use(pState, pLevel, pPos, pPlayer, pHand, pHit));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MachineBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return (lvl, pos, stt, te) -> {
            if (te instanceof MachineBlockEntity machine) {
                if (lvl.isClientSide()) machine.tickClient();
                else machine.tickServer();
            }
        };
    }

    private Optional<MachineBlockEntity> getBlockEntity(BlockGetter world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof MachineBlockEntity machineBe
            ? Optional.of(machineBe)
            : Optional.empty();
    }
}
