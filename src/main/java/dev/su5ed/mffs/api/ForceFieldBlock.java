package dev.su5ed.mffs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.Optional;

/**
 * Force Field Block that is projected as part of a force field
 */
public interface ForceFieldBlock {
    /**
     * Model Property holding a block whose model is used for camouflaging force field blocks
     */
    ModelProperty<BlockState> CAMOUFLAGE_BLOCK = new ModelProperty<>();

    /**
     * Get the projector that created this force field block
     *
     * @param level the level to look in
     * @param pos   the position to search
     * @return the force field block's projector
     */
    Optional<Projector> getProjector(BlockGetter level, BlockPos pos);
}