package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.network.DrawHologramPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DisintegrationModule extends BaseModule {
    private final Set<BlockPos> activeBlocks = ConcurrentHashMap.newKeySet();

    public DisintegrationModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public ProjectAction onSelect(Projector projector, BlockPos pos) {
        if (this.activeBlocks.contains(pos)) {
            // Already scheduled for disintegration; hold off until removal completes.
            return ProjectAction.SKIP;
        }
        World world = projector.be().getWorld();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        // Only actively claim non-air, non-liquid, destructible blocks for disintegration.
        // Indestructible blocks (hardness == -1, e.g. FF blocks pending drain, bedrock) and
        // air positions are passed through so normal force field projection can handle them.
        if (!block.isAir(state, world, pos) && !ModUtil.isLiquidBlock(block) && state.getBlockHardness(world, pos) != -1) {
            if (projector.hasModule(ModModules.CAMOUFLAGE)) {
                Item blockItem = Item.getItemFromBlock(block);
                if (projector.getAllModuleItemsStream()
                    .noneMatch(s -> ProjectorBlockEntity.getFilterBlock(s).isPresent() && s.getItem() == blockItem)) {
                    // Not in the camo whitelist: leave this position alone entirely.
                    return ProjectAction.SKIP;
                }
            }
            if (this.activeBlocks.size() - 1 >= projector.getModuleCount(ModModules.SPEED) / 3) {
                return ProjectAction.INTERRUPT;
            }
            this.activeBlocks.add(pos);
            return ProjectAction.PROJECT;
        }
        // Air, liquid, or indestructible: nothing to disintegrate — skip so no FF is placed.
        return ProjectAction.SKIP;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        World world = projector.be().getWorld();
        IBlockState state = world.getBlockState(position);
        // Air or indestructible (e.g. FF blocks still draining from pendingRemoval):
        // nothing to do here — skip so the projector does not place a force field block.
        if (state.getBlock().isAir(state, world, position) || state.getBlockHardness(world, position) == -1) {
            this.activeBlocks.remove(position); // clean up if somehow still tracked
            return ProjectAction.SKIP;
        }
        // Destructible block: send the DESTROY visual and schedule removal.
        Vec3d pos = new Vec3d(projector.be().getPos());
        Vec3d target = new Vec3d(position);
        Network.sendToAllAround(
            new DrawHologramPacket(pos, target, DrawHologramPacket.HoloType.DESTROY),
            world, position, 64);

        Block block = state.getBlock();
        projector.schedule(39, () -> {
            if (projector.hasModule(ModModules.COLLECTION)) {
                collectBlock(projector, world, position, block);
            } else {
                destroyBlock(world, position, block);
            }
            this.activeBlocks.remove(position);
        });
        return ProjectAction.SKIP; // Hold off on FF placement until terrain is removed
    }

    private static void destroyBlock(World world, BlockPos pos, Block block) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == block) {
            block.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private static void collectBlock(Projector projector, World world, BlockPos pos, Block block) {
        IBlockState state = world.getBlockState(pos);
        if (world instanceof WorldServer && state.getBlock() == block) {
            List<ItemStack> drops = block.getDrops(world, pos, state, 0);
            for (ItemStack drop : drops) {
                projector.mergeIntoInventory(drop);
            }
            world.setBlockToAir(pos);
        }
    }
}
