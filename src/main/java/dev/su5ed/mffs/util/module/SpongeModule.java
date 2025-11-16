package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.module.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SpongeModule extends BaseModule {
    private final List<BlockPos> removingBlocks = new ArrayList<>();
    private final List<BlockPos> unWaterLoggingBlocks = new ArrayList<>();
    private final List<BlockPos> unWaterAquaticPlants = new ArrayList<>();
    private final List<Block> aquaticPlants = List.of(Blocks.KELP,Blocks.KELP_PLANT, Blocks.SEAGRASS,Blocks.TALL_SEAGRASS);

    public SpongeModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void beforeSelect(Projector projector, Collection<? extends TargetPosPair> field) {
        super.beforeSelect(projector, field);

        if (projector.getTicks() % 60 == 0) {
            List<BlockPos> interiorPoints = new ArrayList<>(projector.getInteriorPoints());
            Collections.shuffle(interiorPoints);
            int maxRemove = 50 + projector.getProjectionSpeed() * 20;
            for (int i = 0, count = 0; i < interiorPoints.size() && count < maxRemove; i++) {
                BlockPos pos = interiorPoints.get(i);
                BlockState state = projector.getCachedBlockState(pos);
                Block block = state.getBlock();
                FluidState fluidState = state.getFluidState();
                if (block instanceof LiquidBlock && !fluidState.isEmpty()) {
                    this.removingBlocks.add(pos);
                    if (fluidState.isSource()) {
                        count++;
                    }
                }
                if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    this.unWaterLoggingBlocks.add(pos);
                }

                if (this.isAquaticPlant(state) && !fluidState.isEmpty()) {
                    this.unWaterAquaticPlants.add(pos);
                }
            }
        }
    }

    @Override
    public void beforeProject(Projector projector) {
        Level level = projector.be().getLevel();
        for (BlockPos pos : this.removingBlocks) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        for (BlockPos pos : this.unWaterLoggingBlocks) {
            BlockState state = level.getBlockState(pos);
            if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.WATERLOGGED, false));
            }
        }
        // Handle underwater aquatic plants and drop items as if broken naturally
        for(BlockPos pos: this.unWaterAquaticPlants){
            BlockState state = level.getBlockState(pos);
            FluidState fluidState = state.getFluidState();
            ItemStack stack = this.getDropForBlock(state);
            if (!fluidState.isEmpty()) {
                if(stack != ItemStack.EMPTY){
                    // Drop the corresponding item if it exists
                    level.addFreshEntity(new ItemEntity(level,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,stack));
                }
                // Remove the block itself by setting it to air
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
        this.removingBlocks.clear();
        this.unWaterLoggingBlocks.clear();
        this.unWaterAquaticPlants.clear();
    }
    private boolean isAquaticPlant(BlockState state) {
        return this.aquaticPlants.contains(state.getBlock());
    }
    private ItemStack getDropForBlock(BlockState state) {
        // Return the item to drop for the given block
        // Kelp and Kelp Plant always drop Kelp
        if (state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT)) {
            return new ItemStack(Items.KELP);
        }
        // TODO: Optionally add drop for Seagrass / Tall Seagrass (Silk Touch might be required)
        return ItemStack.EMPTY;
    }
}
