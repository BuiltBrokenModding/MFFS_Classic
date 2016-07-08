package com.mffs.api.security;

import com.mffs.api.IActivatable;
import com.mffs.api.IBiometricIdentifierLink;
import com.mffs.api.fortron.IFortronFrequency;
import com.mffs.api.modules.IModuleAcceptor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * @author Calclavia
 */
public interface IInterdictionMatrix
        extends IInventory, IFortronFrequency, IModuleAcceptor, IBiometricIdentifierLink, IActivatable {
    int getWarningRange();

    int getActionRange();

    boolean mergeIntoInventory(ItemStack paramItemStack);

    Set<Item> getFilteredItems();

    boolean getFilterMode();

    int getFortronCost();
}