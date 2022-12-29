package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.network.DisintegrateBlockPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.BlockDropDelayedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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
            BlockPos bePos = be.getBlockPos();
            Level level = be.getLevel();
			BlockState block = level.getBlockState(position);

            if (!block.isAir()) {
                Vec3 pos = Vec3.atLowerCornerOf(bePos);
                Vec3 target = Vec3.atLowerCornerOf(position);
                Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(position)), new DisintegrateBlockPacket(pos, target, 2));

                // TODO: Modularize this
                ((ProjectorBlockEntity) projector).delayedEvents.add(new BlockDropDelayedEvent(39, block.getBlock(), be.getLevel(), position));

                if (this.blockCount++ >= projector.getModuleCount(ModItems.SPEED_MODULE.get()) / 3) {
                    return ProjectAction.INTERRUPT;
                } else {
                    return ProjectAction.SKIP;
                }
            }
        }

        return ProjectAction.SKIP;
    }
}