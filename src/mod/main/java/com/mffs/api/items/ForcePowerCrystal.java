package com.mffs.api.items;

import com.mffs.MFFS;
import com.mffs.api.ItemManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Created by pwaln on 5/30/2016.
 */
public class ForcePowerCrystal extends Item {

    /* Holds our icons. */
    private IIcon[] damagedIcons = new IIcon[5];

    public ForcePowerCrystal() {
        super();
        setMaxStackSize(1);
        setMaxDamage(100);
    }

    @Override
    public void registerIcons(IIconRegister p_94581_1_) {
        this.itemIcon = p_94581_1_.registerIcon(MFFS.MODID+":ForcePowerCrystal");
        for(int i = 0; i < 5; i++) {
            damagedIcons[i] = p_94581_1_.registerIcon(MFFS.MODID+":ForcePowerCrystal_"+i);
        }
    }

    @Override
    public boolean isRepairable() { return false;}

    /**
     * Gets an icon index based on an item's damage value
     *
     * @param dmg
     */
    @Override
    public IIcon getIconFromDamage(int dmg) {
        if(dmg == 0) {
            return this.itemIcon;
        }
        return damagedIcons[((100 - dmg) / 20)];
    }

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the itemDamage field on ItemStack, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    @Override
    public int getDamage(ItemStack stack) {
        return 101 - ItemManager.getTag(stack).getInteger("ForceEnergy") * 100 / 5_000_000;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack The item being hovered.
     * @param usr The user hovering over the item.
     * @param list A list of tooltips.
     * @param u1 Unkown
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean u1) {
        list.add(String.format("%d FE/%d FE ", ItemManager.getTag(stack).getInteger("ForceEnergy"), 5_000_000));
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
        ItemStack charged = new ItemStack(this, 1);
        charged.setItemDamage(1);
        ItemManager.getTag(charged).setInteger("ForceEnergy", 5_000_000);
        list.add(charged);

        ItemStack empty = new ItemStack(this, 1);
        empty.setItemDamage(100);
        ItemManager.getTag(empty).setInteger("ForceEnergy", 0);
        list.add(empty);
    }
}
