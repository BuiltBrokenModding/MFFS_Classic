package dev.su5ed.mffs.block;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class ProjectorBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 12, 16),
        Block.box(3, 12, 5, 13, 14, 11),
        Block.box(5, 12, 3, 11, 14, 13),
        Block.box(4, 12, 4, 12, 14, 12)
    );

    public ProjectorBlock(Properties properties) {
        super(properties, ModObjects.PROJECTOR_BLOCK_ENTITY::get);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return Optional.ofNullable(level.getBlockEntity(pos))
            .map(be -> be instanceof Projector p && p.getMode() != null ? 10 : 0)
            .orElseGet(() -> super.getLightEmission(state, level, pos));
    }
}
