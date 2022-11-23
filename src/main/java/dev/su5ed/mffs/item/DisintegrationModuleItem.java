package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.BlockDropDelayedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
			BlockState block = be.getLevel().getBlockState(position);

            if (!block.isAir()) {
				// TODO
//                PacketManager.sendPacketToClients(PacketManager.getPacket(ModularForceFieldSystem.CHANNEL, (TileEntity) projector, TilePacketType.FXS.ordinal(), 2, position.intX(), position.intY(), position.intZ()), ((TileEntity) projector).worldObj);

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