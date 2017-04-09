package com.mffs.common.items.card;

import com.mffs.ModularForcefieldSystem;
import com.mffs.api.card.ICardIdentification;
import com.mffs.api.security.Permission;
import com.mffs.api.utils.Util;
import com.mffs.common.net.IPacketReceiver_Item;
import com.mffs.common.net.packet.ItemByteToggle;
import com.mffs.common.net.packet.ItemStringToggle;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemCardID extends ItemCardBlank implements ICardIdentification, IPacketReceiver_Item
{

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     *
     * @param stack
     * @param entity
     * @param hit
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase hit)
    {
        if (entity instanceof EntityPlayer)
        {
            setUsername(stack, ((EntityPlayer) entity).getGameProfile().getName());
        }
        return false;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param list
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy)
    {
        String user = getUsername(stack);
        list.add(user != null && !user.isEmpty() ? LanguageRegistry.instance().getStringLocalization("info.cardIdentification.text") + " " + user
                : LanguageRegistry.instance().getStringLocalization("info.cardIdentification.empty"));

        String tooltip = "";
        for (Permission perm : Permission.values())
        {
            if (hasPermission(stack, perm))
            {
                tooltip += (tooltip.isEmpty() ? "" : ", ") + LanguageRegistry.instance().getStringLocalization("gui." + perm.name() + ".name");
            }
        }

        if (tooltip != null && tooltip.length() > 0)
        {
            list.addAll(Util.sepString(tooltip, 25));
        }

    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     *
     * @param stack
     * @param world
     * @param usr
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer usr)
    {
        if (!world.isRemote)
        {
            if (usr.isSneaking())
            {
                setUsername(stack, usr.getGameProfile().getName());
            }
            else
            {
                usr.openGui(ModularForcefieldSystem.modularForcefieldSystem_mod, 1, world, 0, 0, 0);
            }
        }
        return stack;
    }

    @Override
    public boolean hasPermission(ItemStack paramItemStack, Permission paramPermission)
    {
        return Util.getTag(paramItemStack).getBoolean("mffs_permission_" + paramPermission.ordinal());
    }

    @Override
    public boolean addPermission(ItemStack paramItemStack, Permission paramPermission)
    {
        Util.getTag(paramItemStack).setBoolean("mffs_permission_" + paramPermission.ordinal(), true);
        return false;
    }

    @Override
    public boolean removePermission(ItemStack paramItemStack, Permission paramPermission)
    {
        Util.getTag(paramItemStack).setBoolean("mffs_permission_" + paramPermission.ordinal(), false);
        return false;
    }

    @Override
    public String getUsername(ItemStack paramItemStack)
    {
        return Util.getTag(paramItemStack).getString("mffs_name");
    }

    @Override
    public void setUsername(ItemStack paramItemStack, String paramString)
    {
        Util.getTag(paramItemStack).setString("mffs_name", paramString);
    }

    @Override
    public IMessage handleMessage(IMessage message, ItemStack stack)
    {
        if (message instanceof ItemByteToggle)
        {
            ItemByteToggle tog = (ItemByteToggle) message;
            Permission perm = Permission.getPerm(tog.toggleId);
            if (perm != null)
            {
                NBTTagCompound tag = Util.getTag(stack);
                tag.setBoolean("mffs_permission_" + tog.toggleId, !tag.getBoolean("mffs_permission_" + tog.toggleId));
                return null;
            }
        }
        else if (message instanceof ItemStringToggle)
        {
            ItemStringToggle tog = (ItemStringToggle) message;
            setUsername(stack, tog.text);
            return null;
        }
        return null;
    }

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this, " W ", "WCW", " W ", 'W', Items.redstone, 'C', Item.itemRegistry.getObject("mffs:cardBlank")));
    }
}
