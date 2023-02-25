package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.network.DrawHologramPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.Optional;

public class StabilizationModule extends ModuleBase {
    private int blockCount = 0;

    public StabilizationModule() {
        super(20);
    }

    @Override
    public boolean beforeProject(Projector projector, Collection<BlockPos> fields) {
        this.blockCount = 0;
        return false;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        if (projector.getTicks() % 40 == 0) {
            BlockEntity be = (BlockEntity) projector;
            Level level = be.getLevel();
            BlockPos pos = be.getBlockPos();

            for (Direction side : Direction.values()) {
                IItemHandler handler = Optional.ofNullable(level.getBlockEntity(pos.relative(side)))
                    .flatMap(neighbor -> neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve())
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
                                Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(position)), new DrawHologramPacket(start, target, DrawHologramPacket.Type.CONSTRUCT));

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
