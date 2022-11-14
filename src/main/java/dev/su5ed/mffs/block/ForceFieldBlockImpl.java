package dev.su5ed.mffs.block;

import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class ForceFieldBlockImpl extends Block implements ForceFieldBlock, EntityBlock {
    
    public ForceFieldBlockImpl() {
        super(Properties.of(Material.GLASS)
            .destroyTime(-1)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false));
    }

    @Override
    public Projector getProjector(LevelAccessor level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof ForceFieldBlockEntity forceField ? forceField.getProjector() : null;
    }

    @Override
    public void weakenForceField(Level level, BlockPos pos, int joules) {
        Projector projector = getProjector(level, pos);
        if (projector instanceof FortronStorage storage) {
            storage.provideFortron(joules, IFluidHandler.FluidAction.EXECUTE);
        }
        level.removeBlock(pos, false);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModObjects.FORCE_FIELD_BLOCK_ENTITY.get().create(pos, state); 
    }
}
