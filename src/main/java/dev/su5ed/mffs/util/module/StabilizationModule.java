package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.network.DrawHologramPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class StabilizationModule extends BaseModule {
    private final List<BlockPos> activeBlocks = new ArrayList<>();

    public StabilizationModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void beforeProject(Projector projector) {}

    @Override
    public ProjectAction onSelect(Projector projector, BlockPos pos) {
        // Already scheduled for this selection pass — don't double-commit.
        if (this.activeBlocks.contains(pos)) return ProjectAction.SKIP;

        World world = projector.be().getWorld();
        IBlockState current = world.getBlockState(pos);

        // Only consider air positions (or our own FF block — conversion path).
        boolean isPlaceable = current.getBlock().isAir(current, world, pos)
            || current.getBlock() == ModBlocks.FORCE_FIELD;
        if (!isPlaceable) return ProjectAction.SKIP;

        // Check that at least one placeable ItemBlock exists in an adjacent inventory.
        // We only peek here (simulate=true equivalent via getStackInSlot) — actual extraction
        // happens in onProject after the 39-tick visual delay.
        BlockPos projPos = projector.be().getPos();
        boolean hasBlock = false;
        outer:
        for (EnumFacing side : EnumFacing.values()) {
            TileEntity neighbor = world.getTileEntity(projPos.offset(side));
            if (neighbor != null && neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
                IItemHandler handler = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
                if (handler != null) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        if (stack.getItem() instanceof ItemBlock ib && !ModUtil.isLiquidBlock(ib.getBlock())) {
                            hasBlock = true;
                            break outer;
                        }
                    }
                }
            }
        }
        if (!hasBlock) return ProjectAction.SKIP;

        // Gate concurrency: base of 1 + (speed modules / 3) simultaneous in-flight placements.
        // This matches DisintegrationModule's formula so speed modules feel consistent.
        if (this.activeBlocks.size() - 1 >= projector.getModuleCount(ModModules.SPEED) / 3) {
            return ProjectAction.INTERRUPT;
        }
        this.activeBlocks.add(pos);
        return ProjectAction.PROJECT;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        World world = projector.be().getWorld();
        BlockPos projPos = projector.be().getPos();

        // Send the beam + materializing hologram overlay immediately.
        // The overlay particle lives for ~40 ticks, giving the visual "1-2 second build" effect.
        Vec3d start = new Vec3d(projPos);
        Vec3d target = new Vec3d(position);
        Network.sendToAllAround(
            new DrawHologramPacket(start, target, DrawHologramPacket.HoloType.CONSTRUCT),
            world, position, 64);

        // Schedule actual block placement for 39 ticks later (matches Disintegration delay).
        // The visual appears first; the block solidifies as the overlay fades.
        projector.schedule(39, () -> {
            IBlockState current = world.getBlockState(position);
            // Guard: if someone filled the position in the meantime, just release it.
            boolean stillPlaceable = current.getBlock().isAir(current, world, position)
                || current.getBlock() == ModBlocks.FORCE_FIELD;
            if (stillPlaceable) {
                for (EnumFacing side : EnumFacing.values()) {
                    TileEntity neighbor = world.getTileEntity(projPos.offset(side));
                    if (neighbor != null && neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
                        IItemHandler handler = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
                        if (handler != null) {
                            for (int i = 0; i < handler.getSlots(); i++) {
                                ItemStack slotStack = handler.getStackInSlot(i);
                                if (slotStack.getItem() instanceof ItemBlock ib) {
                                    Block block = ib.getBlock();
                                    @SuppressWarnings("deprecation")
                                    IBlockState state = block.getStateFromMeta(ib.getMetadata(slotStack.getItemDamage()));
                                    if (!ModUtil.isLiquidBlock(block) && world.setBlockState(position, state)) {
                                        handler.extractItem(i, 1, false);
                                        this.activeBlocks.remove(position);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Placement failed (position occupied or inventory empty) — release the slot.
            this.activeBlocks.remove(position);
        });

        // PLACED: projector tracks this position in projectedBlocks now (before the visual gap
        // expires) so the selection pipeline doesn't re-queue it in the next cycle.
        return ProjectAction.PLACED;
    }
}

