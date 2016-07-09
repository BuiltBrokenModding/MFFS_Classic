package com.mffs.common.event;

import com.mffs.api.event.EventTimedTask;
import com.mffs.api.vector.Vector3D;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Created by pwaln on 7/6/2016.
 */
public final class DelayedBlockDropEvent extends EventTimedTask {

    /* The world where this event occurs */
    World world;

    /* The location this event occurs */
    Vector3D vec;

    /**
     * Constructor.
     *
     * @param tick The number of ticks till execution.
     */
    public DelayedBlockDropEvent(int tick, World world, Vector3D vec) {
        super(tick, null);
        this.vec = vec;
        this.world = world;
    }

    /**
     * Execute the desired action.
     */
    @Override
    public void execute() {
        Block block = vec.getBlock(world);
        if (block != null) {
            block.dropBlockAsItem(world, vec.intX(), vec.intY(), vec.intZ(), world.getBlockMetadata(vec.intX(), vec.intY(), vec.intZ()), 0);
        }
        world.setBlockToAir(vec.intX(), vec.intY(), vec.intZ());
    }
}
