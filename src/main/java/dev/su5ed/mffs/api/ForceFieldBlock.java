package dev.su5ed.mffs.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Optional;

/**
 * Force Field Block that is projected as part of a force field.
 */
public interface ForceFieldBlock {
    /**
     * Unlisted property holding the camouflage block state for model quad swapping.
     * Set in {@code getExtendedState()}, read in {@code ForceFieldBlockModel.getQuads()}.
     */
    IUnlistedProperty<IBlockState> CAMOUFLAGE_PROPERTY = new IUnlistedProperty<IBlockState>() {
        @Override public String getName() { return "camouflage"; }
        @Override public boolean isValid(IBlockState value) { return true; }
        @Override public Class<IBlockState> getType() { return IBlockState.class; }
        @Override public String valueToString(IBlockState value) { return value.toString(); }
    };

    /**
     * Get the projector that created this force field block.
     *
     * @param world the world to look in
     * @param pos   the position to search
     * @return the force field block's projector
     */
    Optional<Projector> getProjector(IBlockAccess world, BlockPos pos);
}
