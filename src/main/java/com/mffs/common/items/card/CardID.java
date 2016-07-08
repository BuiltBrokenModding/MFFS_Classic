package com.mffs.common.items.card;

import com.mffs.RegisterManager;
import com.mffs.api.card.ICardIdentification;
import com.mffs.api.security.Permission;
import com.mffs.api.utils.Util;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Calclavia
 */
public class CardID extends CardBlank implements ICardIdentification {

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     *
     * @param stack
     * @param entity
     * @param hit
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase hit) {
        if (entity instanceof EntityPlayer) {
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
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        String user = getUsername(stack);
        list.add(user != null && !user.isEmpty() ? LanguageRegistry.instance().getStringLocalization("info.cardIdentification.username") + " " + user
                : LanguageRegistry.instance().getStringLocalization("info.cardIdentification.empty"));

        String tooltip = "";
        for (Permission perm : Permission.values()) {
            if (hasPermission(stack, perm)) {
                tooltip = (tooltip.isEmpty() ? "" : ", ") + LanguageRegistry.instance().getStringLocalization("gui." + perm.name() + ".name");
            }
        }
        if (tooltip != null && tooltip.length() > 0) {
            list.add(Util.sepString(tooltip, 25));
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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer usr) {
        setUsername(stack, usr.getGameProfile().getName());
        return stack;
    }

    @Override
    public boolean hasPermission(ItemStack paramItemStack, Permission paramPermission) {
        return RegisterManager.getTag(paramItemStack).getBoolean("mffs_permission_" + paramPermission.ordinal());
    }

    @Override
    public boolean addPermission(ItemStack paramItemStack, Permission paramPermission) {
        RegisterManager.getTag(paramItemStack).setBoolean("mffs_permission_" + paramPermission.ordinal(), true);
        return false;
    }

    @Override
    public boolean removePermission(ItemStack paramItemStack, Permission paramPermission) {
        RegisterManager.getTag(paramItemStack).setBoolean("mffs_permission_" + paramPermission.ordinal(), false);
        return false;
    }

    @Override
    public String getUsername(ItemStack paramItemStack) {
        return RegisterManager.getTag(paramItemStack).getString("mffs_name");
    }

    @Override
    public void setUsername(ItemStack paramItemStack, String paramString) {
        RegisterManager.getTag(paramItemStack).setString("mffs_name", paramString);
    }
}
