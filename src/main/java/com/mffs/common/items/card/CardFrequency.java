package com.mffs.common.items.card;

import com.mffs.RegisterManager;
import com.mffs.api.IBlockFrequency;
import com.mffs.api.IItemFrequency;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Calclavia
 */
public class CardFrequency extends CardBlank implements IItemFrequency {
    /**
     * @param itemStack - Pass an ItemStack if dealing with items with frequencies.
     * @return The frequency of this object.
     */
    @Override
    public int getFrequency(ItemStack itemStack) {
        if (itemStack != null) {
            return RegisterManager.getTag(itemStack).getInteger("mffs_freq");
        }
        return 0;
    }

    /**
     * Sets the frequency
     *
     * @param frequency - The frequency of this object.
     * @param itemStack - Pass an ItemStack if dealing with items with frequencies.
     */
    @Override
    public void setFrequency(int frequency, ItemStack itemStack) {
        if (itemStack != null) {
            RegisterManager.getTag(itemStack).setInteger("mffs_freq", frequency);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        int freq = getFrequency(stack);
        if (freq == 0) {
            super.addInformation(stack, usr, list, dummy);
        } else {
            list.add(EnumChatFormatting.GREEN + LanguageRegistry.instance().getStringLocalization("info.cardFrequency.freq") + " " + freq);
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     *
     * @param stack
     * @param wrld
     * @param usr
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World wrld, EntityPlayer usr) {
        if (wrld.isRemote) return stack;
        if (usr.isSneaking()) {
            setFrequency(wrld.rand.nextInt((int) Math.pow(10, 5)), stack);
            usr.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + LanguageRegistry.instance().getStringLocalization("message.cardFrequency.generated") + " " + getFrequency(stack)));
        }
        return stack;
    }

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @param stack  The Item Stack
     * @param player The Player that used the item
     * @param world  The Current World
     * @param x      Target X Position
     * @param y      Target Y Position
     * @param z      Target Z Position
     * @param side   The side of the target hit
     * @param hitX
     * @param hitY
     * @param hitZ   @return Return true to prevent any further processing.
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IBlockFrequency) {
            if (!world.isRemote) {
                int freq = getFrequency(stack);
                world.markBlockForUpdate(x, y, z);
                ((IBlockFrequency) tile).setFrequency(freq);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + LanguageRegistry.instance().getStringLocalization("message.cardFrequency.set").replaceAll("%p", "" + freq)));
            }
            return true;
        }
        return false;
    }
}
