package com.mffs.api.event;

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

    /**
     * @param world
     * @param beforeX
     * @param beforeY
     * @param beforeZ
     * @param afterX
     * @param afterY
     * @param afterZ
     */
    public EventForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
        super(world);
        this.beforeX = beforeX;
        this.beforeY = beforeY;
        this.beforeZ = beforeZ;
        this.afterX = afterX;
        this.afterY = afterY;
        this.afterZ = afterZ;
    }
}
