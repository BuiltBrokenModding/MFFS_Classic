package com.builtbroken.mffs.prefab.tile;

import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mffs.api.IBiometricIdentifierLink;
import com.builtbroken.mffs.api.IBlockFrequency;
import com.builtbroken.mffs.api.card.ICoordLink;
import com.builtbroken.mffs.api.fortron.FrequencyGrid;
import com.builtbroken.mffs.api.security.IBiometricIdentifier;
import com.builtbroken.mffs.api.security.Permission;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pwaln on 6/1/2016.
 */
@Deprecated //Being converted to node system
public abstract class TileFrequency extends TileMFFSInventory implements IBlockFrequency, IBiometricIdentifierLink
{
    /* Frequency of this tile */
    private int frequency;

    @Override
    public void start()
    {
        FrequencyGrid.instance().register(this);
    }

    @Override
    public IBiometricIdentifier getBiometricIdentifier()
    {
        if (getBiometricIdentifiers().size() > 0)
        {
            return (IBiometricIdentifier) getBiometricIdentifiers().toArray()[0];
        }
        return null;
    }

    @Override
    public void invalidate()
    {
        FrequencyGrid.instance().unregister(this);
        super.invalidate();
    }

    @Override
    public Set<IBiometricIdentifier> getBiometricIdentifiers()
    {
        Set<IBiometricIdentifier> list = new HashSet();

        //Collect locations from link cards
        ItemStack itemStack = getStackInSlot(0);
        if (itemStack != null && itemStack.getItem() instanceof ICoordLink)
        {
            Location linkedPosition = ((ICoordLink) itemStack.getItem()).getLink(itemStack);

            if (linkedPosition != null)
            {
                TileEntity tileEntity = linkedPosition.getTileEntity(this.worldObj);

                if ((linkedPosition != null) && ((tileEntity instanceof IBiometricIdentifier)))
                {
                    list.add((IBiometricIdentifier) tileEntity);
                }
            }
        }

        //Collect locations from grid
        for (IBlockFrequency tileEntity : FrequencyGrid.instance().get(getFrequency()))
        {
            if ((tileEntity instanceof IBiometricIdentifier))
            {
                list.add((IBiometricIdentifier) tileEntity);
            }
        }

        return list;
    }

    /**
     * @return The frequency of this object.
     */
    @Override
    public int getFrequency()
    {
        return this.frequency;
    }

    /**
     * Sets the frequency
     *
     * @param frequency - The frequency of this object.
     */
    @Override
    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("frequency", frequency);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.frequency = nbt.getInteger("frequency");
    }

    @Override
    public boolean canBeRemoved(EntityPlayer player)
    {
        if (player.isSneaking())
        {
            IBiometricIdentifier bio = getBiometricIdentifier();
            if (bio != null && !bio.isAccessGranted(player.getGameProfile().getName(), Permission.CONFIGURE))
            {
                player.addChatMessage(new ChatComponentText("[SECURITY]Cannot remove machine! Access denied!"));
                return false;
            }
            return super.canBeRemoved(player);
        }
        return false;
    }
}
