package com.mffs.common.tile;

import com.mffs.api.IBiometricIdentifierLink;
import com.mffs.api.IBlockFrequency;
import com.mffs.api.card.ICoordLink;
import com.mffs.api.fortron.FrequencyGrid;
import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.common.net.packet.ChangeFrequency;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import mekanism.api.Coord4D;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pwaln on 6/1/2016.
 */
public abstract class TileFrequency extends TileMFFSInventory implements IBlockFrequency, IBiometricIdentifierLink {

    /* Frequency of this tile */
    private int frequency;

    @Override
    public void validate() {
        //start();
        super.validate();
    }

    @Override
    public void start() {
        FrequencyGrid.instance().register(this);
    }

    @Override
    public IBiometricIdentifier getBiometricIdentifier() {

        if (getBiometricIdentifiers().size() > 0) {
            return (IBiometricIdentifier) getBiometricIdentifiers().toArray()[0];
        }
        return null;
    }

    @Override
    public void invalidate() {
        FrequencyGrid.instance().unregister(this);
        super.invalidate();
    }

    @Override
    public Set<IBiometricIdentifier> getBiometricIdentifiers() {
        Set<IBiometricIdentifier> list = new HashSet();


        ItemStack itemStack = getStackInSlot(0);
        if (itemStack != null && itemStack.getItem() instanceof ICoordLink) {
            Coord4D linkedPosition = ((ICoordLink) itemStack.getItem()).getLink(itemStack);

            if (linkedPosition != null) {
                TileEntity tileEntity = linkedPosition.getTileEntity(this.worldObj);

                if ((linkedPosition != null) && ((tileEntity instanceof IBiometricIdentifier))) {
                    list.add((IBiometricIdentifier) tileEntity);
                }
            }
        }

        for (IBlockFrequency tileEntity : FrequencyGrid.instance().get(getFrequency())) {
            if ((tileEntity instanceof IBiometricIdentifier)) {
                list.add((IBiometricIdentifier) tileEntity);
            }
        }

        return list;
    }

    /**
     * @return The frequency of this object.
     */
    @Override
    public int getFrequency() {
        return this.frequency;
    }

    /**
     * Sets the frequency
     *
     * @param frequency - The frequency of this object.
     */
    @Override
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    @Override
    public IMessage handleMessage(IMessage imessage) {
        if (imessage instanceof ChangeFrequency) {
            this.frequency = ((ChangeFrequency) imessage).getFrequency();
            return null;
        }
        return super.handleMessage(imessage);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("mffs_freq", frequency);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.frequency = nbt.getInteger("mffs_freq");
    }
}
