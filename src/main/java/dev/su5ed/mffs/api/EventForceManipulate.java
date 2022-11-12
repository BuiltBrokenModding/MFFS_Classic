/**
 *
 */
package dev.su5ed.mffs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Events for the Force Manipulator
 *
 * @author Calclavia
 */
public abstract class EventForceManipulate extends LevelEvent {
    private final BlockPos beforePos;
    private final BlockPos afterPos;
	
    public EventForceManipulate(LevelAccessor level, BlockPos beforePos, BlockPos afterPos) {
        super(level);
        
        this.beforePos = beforePos;
        this.afterPos = afterPos;
    }

    public BlockPos getBeforePos() {
        return this.beforePos;
    }

    public BlockPos getAfterPos() {
        return this.afterPos;
    }

    /**
     * Called every single time a block is checked if it can be manipulated by the Force
     * Manipulator.
     *
     * @author Calclavia
     */
    @Cancelable
    public static class EventCheckForceManipulate extends EventForceManipulate {
        public EventCheckForceManipulate(LevelAccessor level, BlockPos beforePos, BlockPos afterPos) {
            super(level, beforePos, afterPos);
        }
    }

    /**
     * Called right before the TileEntity is moved. After this function is called, the force
     * manipulator will write all TileEntity data into NBT and remove the TileEntity block. A new
     * TileEntity class will be instantiated after words in the new position. This can be canceled
     * and the
     * block will then not move at all.
     */
    @Cancelable
    public static class EventPreForceManipulate extends EventForceManipulate {
        public EventPreForceManipulate(LevelAccessor level, BlockPos beforePos, BlockPos afterPos) {
            super(level, beforePos, afterPos);
        }
    }

    /**
     * Called after a block is moved by the Force Manipulator and when all move operations are
     * completed. This is called before the placed block get notified of neighborBlockChange.
     *
     * @author Calclavia
     */
    public static class EventPostForceManipulate extends EventForceManipulate {
        public EventPostForceManipulate(LevelAccessor level, BlockPos beforePos, BlockPos afterPos) {
            super(level, beforePos, afterPos);
        }
    }
}
