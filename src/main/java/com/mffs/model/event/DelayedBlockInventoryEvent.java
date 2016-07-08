package com.mffs.model.event;

import com.mffs.api.event.EventTimedTask;
import com.mffs.api.vector.Vector3D;
import com.mffs.model.tile.TileMFFSInventory;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by pwaln on 7/6/2016.
 */
public class DelayedBlockInventoryEvent extends EventTimedTask {

    /* The world this event is happening in */
    private World world;

    /* The vector in this event */
    private Vector3D vec;

    /* The inventory associated with this event */
    private TileMFFSInventory inventory;

    /**
     * Constructor.
     *
     * @param tick The number of ticks till execution.
     */
    public DelayedBlockInventoryEvent(int tick, World world, Vector3D vec, TileMFFSInventory inv) {
        super(tick, null);
        this.world = world;
        this.vec = vec;
        this.inventory = inv;
    }

    /**
     * Execute the desired action.
     */
    @Override
    public void execute() {
        Block block = vec.getBlock(world);
        if(block != null) {
            ArrayList<ItemStack> items = block.getDrops(world, vec.intX(), vec.intY(), vec.intZ(), vec.getBlockMetadata(world), 0);
            for(ItemStack stack : items) {
                if(!inventory.mergeIntoInventory(stack)) {
                   //Logger wont let me use it WTF
                    return;
                }
            }
            world.setBlockToAir(vec.intX(), vec.intY(), vec.intZ());
        }
    }
}
