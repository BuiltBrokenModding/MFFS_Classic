package dev.su5ed.mffs.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Events for the Force Manipulator
 *
 * @author Calclavia
 */
public abstract class EventForceManipulate extends Event {
    private final World world;
    private final BlockPos beforePos;

    public EventForceManipulate(World world, BlockPos beforePos) {
        this.world     = world;
        this.beforePos = beforePos;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getBeforePos() {
        return this.beforePos;
    }

    /**
     * Called right before the TileEntity is moved. After this function is called, the force
     * manipulator will write all TileEntity data into NBT and remove the TileEntity block. A new
     * TileEntity class will be instantiated after words in the new position. This can be canceled
     * and the block will then not move at all.
     */
    @Cancelable
    public static class EventPreForceManipulate extends EventForceManipulate {
        public EventPreForceManipulate(World world, BlockPos beforePos) {
            super(world, beforePos);
        }
    }
}
