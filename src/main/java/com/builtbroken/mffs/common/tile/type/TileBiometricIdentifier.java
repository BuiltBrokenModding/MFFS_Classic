package com.builtbroken.mffs.common.tile.type;

import com.builtbroken.mffs.ModularForcefieldSystem;
import com.builtbroken.mffs.api.IItemFrequency;
import com.builtbroken.mffs.api.card.ICardIdentification;
import com.builtbroken.mffs.api.security.IBiometricIdentifier;
import com.builtbroken.mffs.api.security.Permission;
import com.builtbroken.mffs.common.tile.TileFrequency;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class TileBiometricIdentifier extends TileFrequency implements IBiometricIdentifier
{

    @Override
    public boolean isAccessGranted(String paramString, Permission paramPermission)
    {
        if (!isActive())
        {
            return true;
        }
        //TODO: Check for OP
        for (int slot = 1; slot < getSizeInventory(); slot++)
        {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null)
            {
                ICardIdentification card = (ICardIdentification) stack.getItem();
                if (card.getUsername(stack).equals(paramString))
                {
                    return card.hasPermission(stack, paramPermission);
                }
            }
        }
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        return 37;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param slot
     * @param stack
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (slot == 0)
        {
            return stack.getItem() instanceof IItemFrequency;
        }
        return stack.getItem() instanceof ICardIdentification;
    }

    @Override
    public Set<IBiometricIdentifier> getBiometricIdentifiers()
    {
        Set<IBiometricIdentifier> set = new HashSet();
        set.add(this);
        return set;
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entityPlayer)
    {
        List<ItemStack> stack = super.getRemovedItems(entityPlayer);
        stack.add(new ItemStack(ModularForcefieldSystem.biometricIdentifier));
        return stack;
    }
}
