package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.network.DisintegrateBlockPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;

public class DisintegrationModuleItem extends ModuleItem {
    private int blockCount = 0;

    public DisintegrationModuleItem() {
        super(ModItems.itemProperties().stacksTo(1), 20);
    }

    @Override
    public boolean onProject(Projector projector, Set<BlockPos> fields) {
        this.blockCount = 0;
        return false;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        if (projector.getTicks() % 40 == 0) {
            BlockEntity be = (BlockEntity) projector;
            Level level = be.getLevel();
            BlockState block = level.getBlockState(position);

            if (!block.isAir()) {
                if (projector.hasModule(ModItems.CAMOUFLAGE_MODULE.get())) {
                    Item blockItem = block.getBlock().asItem();
                    boolean contains = projector.getAllModuleItemsStream()
                        .anyMatch(stack -> ProjectorBlockEntity.getFilterBlock(stack).isPresent() && stack.is(blockItem));
                    if (!contains) {
                        return ProjectAction.SKIP;
                    }
                }

                Vec3 pos = Vec3.atLowerCornerOf(be.getBlockPos());
                Vec3 target = Vec3.atLowerCornerOf(position);
                Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(position)), new DisintegrateBlockPacket(pos, target, 2));

                projector.schedule(39, () -> {
                    if (projector.hasModule(ModItems.COLLECTION_MODULE.get())) {
                        collectBlock(projector, level, position, block.getBlock());
                    }
                    else {
                        destroyBlock(level, position, block.getBlock());
                    }
                });

                if (this.blockCount++ >= projector.getModuleCount(ModItems.SPEED_MODULE.get()) / 3) {
                    return ProjectAction.INTERRUPT;
                } else {
                    return ProjectAction.SKIP;
                }
            }
        }

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