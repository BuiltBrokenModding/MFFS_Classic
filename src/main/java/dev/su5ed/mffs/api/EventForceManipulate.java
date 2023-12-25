/**
 *
 */
package dev.su5ed.mffs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Events for the Force Manipulator
 *
 * @author Calclavia
 */
public abstract class EventForceManipulate extends LevelEvent {
    private final BlockPos beforePos;

    public EventForceManipulate(LevelAccessor level, BlockPos beforePos) {
        super(level);

        this.beforePos = beforePos;
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
    public static class EventPreForceManipulate extends EventForceManipulate implements ICancellableEvent {
        public EventPreForceManipulate(LevelAccessor level, BlockPos beforePos) {
            super(level, beforePos);
        }
    }
}
