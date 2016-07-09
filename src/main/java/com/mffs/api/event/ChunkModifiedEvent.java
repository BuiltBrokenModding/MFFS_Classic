package com.mffs.api.event;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;

/**
 * Created by pwaln on 7/8/2016.
 */
public class ChunkModifiedEvent extends ChunkEvent {

    /* The coordinates of this event */
    public final int x, y, z;

    /* THe block of this event */
    public final Block block;

    /* The mtea of the block */
    public final int meta;

    /**
     * @param chunk
     * @param cX
     * @param y
     * @param cZ
     * @param block
     * @param meta
     */
    public ChunkModifiedEvent(Chunk chunk, int cX, int y, int cZ, Block block, int meta) {
        super(chunk);
        this.x = cX;
        this.y = y;
        this.z = cZ;
        this.block = block;
        this.meta = meta;
    }
}
