package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.network.DrawHologramPacket;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class StabilizationModule extends BaseModule {
    private int blockCount = 0;

    public StabilizationModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void beforeProject(Projector projector) {
        this.blockCount = 0;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        if (projector.getTicks() % 40 == 0) {
            Level level = projector.be().getLevel();
            BlockPos pos = projector.be().getBlockPos();

            for (Direction side : Direction.values()) {
                IItemHandler handler = Optional.ofNullable(level.getBlockEntity(pos.relative(side)))
                    .map(neighbor -> neighbor.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, neighbor.getBlockPos(), neighbor.getBlockState(), neighbor, side))
                    .orElse(null);
                if (handler != null) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.extractItem(i, 1, true);

                        if (stack.getItem() instanceof BlockItem blockItem) {
                            Block block = blockItem.getBlock();
                            BlockState state = block.defaultBlockState();
                            if (!state.is(ModTags.STABILIZATION_BLACKLIST) && !ModUtil.isLiquidBlock(block) && level.setBlockAndUpdate(position, state)) {
                                handler.extractItem(i, 1, false);
                                Vec3 start = Vec3.atLowerCornerOf(pos);
                                Vec3 target = Vec3.atLowerCornerOf(position);
                                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(position).getPos(), new DrawHologramPacket(start, target, DrawHologramPacket.Type.CONSTRUCT));

                                return this.blockCount++ >= projector.getModuleCount(ModModules.SPEED) / 3 ? ProjectAction.INTERRUPT : ProjectAction.SKIP;
                            }
                        }
                    }
                }
            }
        }
        return ProjectAction.SKIP;
    }
}
