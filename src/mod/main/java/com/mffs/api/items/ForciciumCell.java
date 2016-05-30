package com.mffs.api.items;

import com.mffs.MFFS;
import com.mffs.api.RegisterManager;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.List;

/**
 * Original MFFS File.
 * Credits: Thunderdark, Calclavia
 */
public class ForciciumCell extends Item {

    /* If the item is activated */
    private boolean isActivated;

    /**
     * Constructor.
     */
    public ForciciumCell() {
        setMaxStackSize(1);
        setMaxDamage(100);
    }

    @Override
    public void registerIcons(IIconRegister p_94581_1_) {
        this.itemIcon = p_94581_1_.registerIcon(MFFS.MODID+":ForciciumCell");
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
        return 101 -  RegisterManager.getTag(stack).getShort("level") * 100 / 1000;
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
        if(!world.isRemote && this.isActivated) {
            short level = RegisterManager.getTag(stack).getShort("level");
            if(level < 1_000 && entity instanceof EntityPlayer) {
                List<Slot> invSlot = ((EntityPlayer)entity).inventoryContainer.inventorySlots;
                Forcicium forc = (Forcicium) Item.itemRegistry.getObject(MFFS.MODID+":Forcicium");
                for(Slot slot : invSlot)
                {
                    ItemStack item = slot.getStack();
                    if(item != null && item.getItem() == forc) {
                        RegisterManager.getTag(stack).setShort("level", (short) (level + 1));
                        slot.decrStackSize(1);
                        return;
                    }
                }
            }
        }
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
        list.add(String.format("%d / %d  Forcicium", RegisterManager.getTag(stack).getShort("level"), 1_000));
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
        if(world.isRemote) {
            return stack;
        }
        this.isActivated = !this.isActivated;
        entity.addChatMessage(new ChatComponentText(LanguageRegistry.instance().getStringLocalization("itemInfo.forciciumCell"+(isActivated ? "Active" : "Inactive"))));
        return stack;
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
        ItemStack sub1 = new ItemStack(this, 1);
        sub1.setItemDamage(1);
        RegisterManager.getTag(sub1).setShort("level", (short) 1_000);
        list.add(sub1);

        ItemStack sub2 = new ItemStack(this, 1);
        sub2.setItemDamage(100);
        RegisterManager.getTag(sub2).setShort("level", (short) 0);
        list.add(sub2);
    }
}
