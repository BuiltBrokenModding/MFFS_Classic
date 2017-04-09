package com.mffs.common.items.modules.interdiction;

import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.api.security.Permission;
import com.mffs.common.items.modules.MatrixModule;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentText;

import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ItemModuleConfiscate extends MatrixModule
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "PEP", "EFE", "PEP",
                'P', Items.ender_pearl,
                'E', Items.ender_eye,
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    @Override
    public boolean onDefend(IInterdictionMatrix matrix, EntityLivingBase entity)
    {
        IInventory inventory = null;
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            IBiometricIdentifier bio = matrix.getBiometricIdentifier();
            if (bio != null && (bio.isAccessGranted(player.getGameProfile().getName(), Permission.BYPASS_CONFISCATION)
                    || bio.isAccessGranted(player.getGameProfile().getName(), Permission.BYPASS_DEFENSE)))
            {
                return false;
            }
            inventory = player.inventory;
        }
        else if (entity instanceof IInventory)
        {
            inventory = (IInventory) entity;
        }

        if (inventory != null)
        {
            int conf_count = 0;
            Set<ItemStack> safe_items = matrix.getFilteredItems();
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++)
            {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack == null || safe_items.contains(stack))
                {
                    continue;
                }
                matrix.mergeIntoInventory(stack);
                inventory.setInventorySlotContents(slot, null);
                conf_count++;
            }
            ;
            matrix.requestFortron(conf_count, true);
            if (entity instanceof EntityPlayer && conf_count > 0)
            {
                ((EntityPlayer) entity).addChatMessage(new ChatComponentText("[" + matrix.getInventoryName() + "] " +
                        LanguageRegistry.instance().getStringLocalization("message.moduleConfiscate.confiscate").replaceAll("%p", "" + conf_count)));
            }
        }
        return super.onDefend(matrix, entity);
    }

}
