package com.mffs.common.tile.type;

import com.mffs.api.card.ICardIdentification;
import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.api.security.Permission;
import com.mffs.common.items.card.CardFrequency;
import com.mffs.common.tile.TileFrequency;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class TileBiometricIdentifier extends TileFrequency implements IBiometricIdentifier {

    /* Slot that gives a copy of cards */
    public static final byte SLOT_COPY = 12;

    public static final byte SLOT_MASTER = 2;

    @Override
    public boolean isAccessGranted(String paramString, Permission paramPermission) {
        if (!isActive())
            return false;
        //TODO: Check for OP
        return false;
    }

    @Override
    public String getOwner() {
        ItemStack card = getStackInSlot(SLOT_MASTER);
        if (card != null && card.getItem() instanceof ICardIdentification) {
            return ((ICardIdentification) card.getItem()).getUsername(card);
        }
        return null;
    }

    @Override
    public ItemStack getManipulatingCard() {
        ItemStack stack = getStackInSlot(1);
        if (stack != null && stack.getItem() instanceof ICardIdentification)
            return stack;
        return null;
    }

    @Override
    public int getSizeInventory() {
        return 13;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void setActive(boolean b) {
        if (getOwner() != null || !b) {
            super.setActive(b);
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param slot
     * @param stack
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 0)
            return stack.getItem() instanceof CardFrequency;
        else if (slot == SLOT_COPY && getManipulatingCard() == null)
            return false;
        return stack.getItem() instanceof ICardIdentification;
    }

    @Override
    public void onSlotsChanged(int... slots) {
        super.onSlotsChanged(slots);
        ItemStack manipul = getManipulatingCard();
        ItemStack copy = getStackInSlot(SLOT_COPY);
        if (manipul != null && copy != null && copy.getItem() instanceof ICardIdentification) {
            ICardIdentification masterCard = (ICardIdentification) manipul.getItem();

            //do a loop of permissions
            //or just do a copy?
            // setInventorySlotContents(SLOT_COPY, manipul.copy());
            String user = masterCard.getUsername(copy);
            inventory[SLOT_COPY] = manipul.copy();
            masterCard.setUsername(inventory[SLOT_COPY], user);
        }
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    @Override
    public IMessage handleMessage(IMessage imessage) {
        return super.handleMessage(imessage);
    }

    @Override
    public Set<IBiometricIdentifier> getBiometricIdentifiers() {
        Set<IBiometricIdentifier> set = new HashSet();
        set.add(this);
        return set;
    }
}
