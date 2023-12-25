package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.network.DrawHologramPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class DisintegrationModule extends BaseModule {
    private final List<BlockPos> activeBlocks = new ArrayList<>();

    public DisintegrationModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public ProjectAction onSelect(Projector projector, BlockPos pos) {
        if (!this.activeBlocks.contains(pos)) {
            Level level = projector.be().getLevel();
            BlockState state = level.getBlockState(pos);

            if (!state.isAir()) {
                Block block = state.getBlock();

                if (projector.hasModule(ModModules.CAMOUFLAGE)) {
                    Item blockItem = block.asItem();
                    if (projector.getAllModuleItemsStream().noneMatch(stack -> ProjectorBlockEntity.getFilterBlock(stack).isPresent() && stack.is(blockItem))) {
                        return ProjectAction.SKIP;
                    }
                }

                if (!state.is(ModTags.DISINTEGRATION_BLACKLIST) && !ModUtil.isLiquidBlock(block)) {
                    if (this.activeBlocks.size() - 1 >= projector.getModuleCount(ModModules.SPEED) / 3) {
                        return ProjectAction.INTERRUPT;
                    }
                    this.activeBlocks.add(pos);
                    return ProjectAction.PROJECT;
                }
            }
        }
        return ProjectAction.SKIP;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        Level level = projector.be().getLevel();
        BlockState state = level.getBlockState(position);
        Vec3 pos = Vec3.atLowerCornerOf(projector.be().getBlockPos());
        Vec3 target = Vec3.atLowerCornerOf(position);
        Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(position)), new DrawHologramPacket(pos, target, DrawHologramPacket.Type.DESTROY));

        projector.schedule(39, () -> {
            if (projector.hasModule(ModModules.COLLECTION)) {
                collectBlock(projector, level, position, state.getBlock());
            } else {
                destroyBlock(level, position, state.getBlock());
            }
            this.activeBlocks.remove(position);
        });

        return ProjectAction.SKIP;
    }

    private static void destroyBlock(Level level, BlockPos pos, Block block) {
        BlockState state = level.getBlockState(pos);
        if (state.is(block)) {
            Block.dropResources(state, level, pos);
            level.removeBlock(pos, false);
        }
    }

    private static void collectBlock(Projector projector, Level level, BlockPos pos, Block block) {
        BlockState state = level.getBlockState(pos);
        if (level instanceof ServerLevel serverLevel && state.is(block)) {
            Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos)).forEach(projector::mergeIntoInventory);
            level.removeBlock(pos, false);
        }
    }
}
