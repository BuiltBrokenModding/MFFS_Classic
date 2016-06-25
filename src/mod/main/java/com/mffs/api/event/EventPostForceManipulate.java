package com.mffs.api.event;

import net.minecraft.world.World;

/**
 * Created by pwaln on 6/6/2016.
 */
public class EventPostForceManipulate extends EventForceManipulate {

    /**
     * @param world
     * @param beforeX
     * @param beforeY
     * @param beforeZ
     * @param afterX
     * @param afterY
     * @param afterZ
     */
    public EventPostForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
        super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
    }
}
