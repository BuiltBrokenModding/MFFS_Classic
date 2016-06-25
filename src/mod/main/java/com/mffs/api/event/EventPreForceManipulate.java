package com.mffs.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.world.World;

/**
 * @author Calclavia
 */
@Cancelable
public class EventPreForceManipulate extends EventForceManipulate {

    /**
     * @param world
     * @param beforeX
     * @param beforeY
     * @param beforeZ
     * @param afterX
     * @param afterY
     * @param afterZ
     */
    public EventPreForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
        super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
    }
}
