package com.mffs.api.items.card;

import com.mffs.api.ItemManager;
import com.mffs.api.Vector4;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Created by pwaln on 5/29/2016.
 */
public abstract class ItemCard extends Item{

    /* We hold the display information here */
    private StringBuffer info = new StringBuffer();

    /* Store the tick count here, only this directory should have access */
    protected short tick;

    /**
     * Called by CraftingManager to determine if an item is reparable.
     *
     * @return True if reparable
     */
    @Override
    public boolean isRepairable() {
        return false;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param item
     * @param usr
     * @param info
     * @param p_77624_4_
     */
    @Override
    public void addInformation(ItemStack item, EntityPlayer usr, List info, boolean p_77624_4_) {
        NBTTagCompound tag = ItemManager.getTag(item);

        info.add("Links To: " + tag.getString("Areaname"));
        if (tag.hasKey("worldname"))
            info.add("World: " + tag.getString("worldname"));
        if (tag.hasKey("linkTarget")) {
            NBTTagCompound coord = tag.getCompoundTag("linkTarget");
            info.add("Coords: X:" + coord.getInteger("x")+ " Y:" +coord.getInteger("y") + " Z:" + coord.getInteger("z"));
        }
        if (tag.hasKey("valid")) {
            info.add(tag.getBoolean("valid") ? "ยง2Valid" : "ยง4Invalid");
        }
    }

    /**
     * Obtains the link Target.
     * @param stack The item we are analyzing.
     * @return The target.
     */
    public Vector4 getTarget(ItemStack stack) {
        NBTTagCompound tag = ItemManager.getTag(stack).getCompoundTag("linkTarget");
        int x = tag.getInteger("x");
        int y = tag.getInteger("y");
        int z = tag.getInteger("z");
        int dim = tag.getInteger("dim");
        return new Vector4(x, y, z, dim);
    }
}
