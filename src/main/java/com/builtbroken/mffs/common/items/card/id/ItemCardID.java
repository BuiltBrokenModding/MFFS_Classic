package com.builtbroken.mffs.common.items.card.id;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mffs.ModularForcefieldSystem;
import com.builtbroken.mffs.api.card.ICardIdentification;
import com.builtbroken.mffs.api.security.Permission;
import com.builtbroken.mffs.api.utils.Util;
import com.builtbroken.mffs.common.items.card.ItemCardBlank;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.LanguageRegistry;
import io.netty.buffer.ByteBuf;
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
public class ItemCardID extends ItemCardBlank implements ICardIdentification, IPacketIDReceiver
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
     * @param player
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            if (player.isSneaking())
            {
                setUsername(stack, player.getGameProfile().getName());
            }
            else
            {
                player.openGui(ModularForcefieldSystem.modularForcefieldSystem_mod, 1, world, player.inventory.currentItem, 0, 0);
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

    public void sendUserNamePacket(EntityPlayer player, String paramString)
    {
        PacketPlayerItem packet = new PacketPlayerItem(player.inventory.currentItem, 0);
        ByteBufUtils.writeUTF8String(packet.data(), paramString);
        Engine.packetHandler.sendToServer(packet);
    }

    public void sendPermPacket(EntityPlayer player, int permID, boolean state)
    {
        Permission perm = Permission.getPerm(permID);
        if (perm != null)
        {
            PacketPlayerItem packet = new PacketPlayerItem(player.inventory.currentItem, 1);
            packet.data().writeInt(permID);
            packet.data().writeBoolean(state);
            Engine.packetHandler.sendToServer(packet);
        }
        else
        {
            //TODO error
        }
    }

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this, " W ", "WCW", " W ", 'W', Items.redstone, 'C', Item.itemRegistry.getObject("mffs:cardBlank")));
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == this)
        {
            if (id == 0)
            {
                //TODO attempt to match name to existing users in case user miss spelled
                String name = ByteBufUtils.readUTF8String(buf);
                ItemStack stack = player.getHeldItem();
                setUsername(stack, name);
                player.inventoryContainer.detectAndSendChanges();
                return true;
            }
            else if (id == 1)
            {
                int permID = buf.readInt();
                Permission perm = Permission.getPerm(permID);
                if (perm != null)
                {
                    NBTTagCompound tag = Util.getTag(player.getHeldItem());
                    tag.setBoolean("mffs_permission_" + permID, buf.readBoolean());
                }
                player.inventoryContainer.detectAndSendChanges();
                return true;
            }
        }
        return false;
    }
}
