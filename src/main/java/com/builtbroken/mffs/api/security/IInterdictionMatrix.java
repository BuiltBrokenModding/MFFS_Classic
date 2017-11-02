package com.builtbroken.mffs.api.security;

import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mffs.api.IActivatable;
import com.builtbroken.mffs.api.IBiometricIdentifierLink;
import com.builtbroken.mffs.api.fortron.IFortronFrequency;
import com.builtbroken.mffs.api.modules.IModuleAcceptor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IInterdictionMatrix extends IInventory, IFortronFrequency, IModuleAcceptor, IBiometricIdentifierLink, IActivatable
{
    Cube getWarningRange();

    Cube getActionRange();

    boolean mergeIntoInventory(ItemStack paramItemStack);

    Set<ItemStack> getFilteredItems();

    boolean getFilterMode();

    int getFortronCost();
}