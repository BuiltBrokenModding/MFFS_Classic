package dev.su5ed.mffs.command;

import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the /mffs server command. (Requires OP permission)
 *
 * Subcommands:
 *   /mffs removeorphans all — scans every loaded chunk
 *   /mffs removeorphans <radius> — limited to chunks within sender's current position
 */
public final class MffsCommand extends CommandBase {

    @Override
    public String getName() {
        return "mffs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mffs removeorphans <all|radius>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "removeorphans");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("removeorphans")) {
            return getListOfStringsMatchingLastWord(args, "all");
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2 || !args[0].equalsIgnoreCase("removeorphans")) {
            throw new WrongUsageException(getUsage(sender));
        }

        String radiusArg = args[1];
        boolean scanAll = radiusArg.equalsIgnoreCase("all");
        int chunkRadius = -1;

        if (!scanAll) {
            chunkRadius = parseInt(radiusArg, 1);
        }

        int removed = 0;

        if (scanAll) {
            for (WorldServer world : server.worlds) {
                removed += removeOrphans(world, null, -1);
            }
        } else {
            // Radius scans require a positional sender (player or command block).
            // Console operators should use "all" instead.
            if (sender.getCommandSenderEntity() == null && !sender.getClass().getSimpleName().equals("DedicatedServer")) {
                // Still allow console by using overworld as the reference world.
                World world = server.worlds[0];
                BlockPos senderPos = sender.getPosition();
                removed += removeOrphans(world, senderPos, chunkRadius);
            } else {
                World world = sender.getEntityWorld();
                BlockPos senderPos = sender.getPosition();
                removed += removeOrphans(world, senderPos, chunkRadius);
            }
        }

        String message = removed == 0
            ? "No orphaned force field blocks found."
            : "Removed " + removed + " orphaned force field block" + (removed == 1 ? "." : "s.");
        sender.sendMessage(new TextComponentString(message));
    }

    /**
     * Scans loaded tile entities for orphaned force field blocks and removes them. 
     *
     * @param world       the world to scan
     * @param center      chunk-filter centre position, or {@code null} to scan all
     * @param chunkRadius chunk radius, ignored when {@code center} is {@code null}
     * @return number of orphaned blocks removed
     */
    private static int removeOrphans(World world, BlockPos center, int chunkRadius) {
        // Snapshot the list to avoid ConcurrentModificationException when removing blocks.
        List<TileEntity> snapshot = new ArrayList<>(world.loadedTileEntityList);
        List<BlockPos> toRemove = new ArrayList<>();

        for (TileEntity te : snapshot) {
            if (!(te instanceof ForceFieldBlockEntity ffe)) continue;

            // Apply chunk-radius filter when requested.
            if (center != null) {
                int cx = ffe.getPos().getX() >> 4;
                int cz = ffe.getPos().getZ() >> 4;
                int ocx = center.getX() >> 4;
                int ocz = center.getZ() >> 4;
                if (Math.abs(cx - ocx) > chunkRadius || Math.abs(cz - ocz) > chunkRadius) {
                    continue;
                }
            }

            if (isOrphan(world, ffe)) {
                toRemove.add(ffe.getPos());
            }
        }

        for (BlockPos pos : toRemove) {
            if (world.getBlockState(pos).getBlock() == ModBlocks.FORCE_FIELD) {
                world.setBlockToAir(pos);
            }
        }

        return toRemove.size();
    }

    /**
     * Returns {@code true} if the given {@link ForceFieldBlockEntity} is considered orphaned:
     * either it has no recorded projector position, or the TileEntity at that position is no
     * longer a live {@link ProjectorBlockEntity}.
     */
    private static boolean isOrphan(World world, ForceFieldBlockEntity ffe) {
        BlockPos projectorPos = ffe.getProjectorPos();
        if (projectorPos == null) return true;
        TileEntity projTE = world.getTileEntity(projectorPos);
        return !(projTE instanceof ProjectorBlockEntity);
    }
}
