package com.mffs.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

/**
 * @author Calclavia
 */
public abstract class EventForceManipulate
        extends WorldEvent {
    public int beforeX;
    public int beforeY;
    public int beforeZ;
    public int afterX;
    public int afterY;
    public int afterZ;

    public EventForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
        super(world);
        this.beforeX = beforeX;
        this.beforeY = beforeY;
        this.beforeZ = beforeZ;
        this.afterX = afterX;
        this.afterY = afterY;
        this.afterZ = afterZ;
    }


    @Cancelable
    public static class EventCheckForceManipulate
            extends EventForceManipulate {
        public EventCheckForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
            super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
        }
    }


    @Cancelable
    public static class EventPreForceManipulate
            extends EventForceManipulate {
        public EventPreForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
            super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
        }
    }


    public static class EventPostForceManipulate
            extends EventForceManipulate {
        public EventPostForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
            super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
        }
    }
}
