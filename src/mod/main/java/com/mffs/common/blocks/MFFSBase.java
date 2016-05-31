package com.mffs.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Created by pwaln on 5/30/2016.
 */
public abstract class MFFSBase extends BlockContainer {

    /* Icons for each block side */
    public IIcon[] icons = new IIcon[4];

    public MFFSBase() {
        super(Material.iron);
        setHardness(100.0F);
    }

    /**
     * Called upon block activation (right click on the block.)
     *
     * @param world
     * @param xPos
     * @param yPos
     * @param zPos
     * @param usr
     * @param side
     * @param d1
     * @param d2
     * @param d3
     */
    @Override
    public boolean onBlockActivated(World world, int xPos, int yPos, int zPos, EntityPlayer usr, int side, float d1, float d2, float d3) {
        if(usr.isClientWorld()) return false;
        if(world.isRemote) return true;
        if(world.getTileEntity(xPos, yPos, zPos) == null) return false;
        if(usr.getItemInUse() != null) {
            Item item = usr.getItemInUse().getItem();
        }
        return super.onBlockActivated(world, xPos, yPos, zPos, usr, side, d1, d2, d3);
    }
}
