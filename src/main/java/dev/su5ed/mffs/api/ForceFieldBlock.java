package dev.su5ed.mffs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public interface ForceFieldBlock {
    ModelProperty<Block> CAMOUFLAGE_BLOCK = new ModelProperty<>();

    @Nullable
    Projector getProjector(LevelAccessor level, BlockPos pos);

    /**
     * Weakens a force field block, destroying it temporarily and draining power from the projector.
     *
     * @param joules - Power to drain.
     */
    void weakenForceField(Level level, BlockPos pos, int joules);
}