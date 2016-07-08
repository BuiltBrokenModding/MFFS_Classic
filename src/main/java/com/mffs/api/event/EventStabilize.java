package com.mffs.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;


/**
 * @author Calclavia
 */
@Cancelable
public class EventStabilize
        extends WorldEvent {
    public final ItemStack itemStack;
    public final int x;
    public final int y;
    public final int z;

    public EventStabilize(World world, int x, int y, int z, ItemStack itemStack) {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
        this.itemStack = itemStack;
    }
}