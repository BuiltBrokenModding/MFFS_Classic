package com.mffs.api.items;

import com.mffs.MFFS;
import com.mffs.api.ItemManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by pwaln on 5/29/2016.
 */
public class ForcicumCell extends Item {

    /* If the item is activated */
    private boolean isActivated;

    /**
     * Constructor.
     */
    public ForcicumCell() {
        setTextureName(MFFS.MODID+"ForciumCell");
    }

    @Override
    public boolean isRepairable() { return false; }

    @Override
    public boolean getHasSubtypes() { return true;}

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the itemDamage field on ItemStack, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    @Override
    public int getDamage(ItemStack stack) {
        return 101 -  ItemManager.getTag(stack).getShort("level") * 100 / 1000;
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     *
     * @param stack
     * @param world
     * @param entity
     * @param u1
     * @param u2
     */
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int u1, boolean u2) {
        super.onUpdate(stack, world, entity, u1, u2);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param list
     * @param u1
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean u1) {
        list.add(String.format("%d / %d  Forcicium", ItemManager.getTag(stack).getShort("level"), 1_000));
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     *
     * @param stack
     * @param world
     * @param entity
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer entity) {
        return super.onItemRightClick(stack, world, entity);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param item
     * @param tab
     * @param list
     */
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        super.getSubItems(item, tab, list);
    }
}
