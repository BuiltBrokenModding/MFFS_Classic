package com.builtbroken.mffs.content.cap;

import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mffs.MFFS;
import com.builtbroken.mffs.MFFSSettings;
import com.builtbroken.mffs.api.card.ICardInfinite;
import com.builtbroken.mffs.api.card.ICoordLink;
import com.builtbroken.mffs.api.fortron.FrequencyGrid;
import com.builtbroken.mffs.api.fortron.IFortronCapacitor;
import com.builtbroken.mffs.api.fortron.IFortronFrequency;
import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.api.utils.FortronHelper;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.TransferMode;
import com.builtbroken.mffs.common.items.card.ItemCardFrequency;
import com.builtbroken.mffs.common.items.card.ItemCardLink;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleScale;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleSpeed;
import com.builtbroken.mffs.common.net.packet.EntityToggle;
import com.builtbroken.mffs.prefab.ModuleInventory;
import com.builtbroken.mffs.prefab.tile.TileModuleAcceptor;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class TileFortronCapacitor extends TileModuleAcceptor implements IFortronCapacitor
{

    /* Current distribution method */
    private TransferMode mode = TransferMode.EQUALIZE;

    /**
     * Constructor.
     */
    public TileFortronCapacitor()
    {
        this.fortronCapacity = 700;
        this.fortronCapacityBoostPerCard = 10;
        this.moduleInventory = new ModuleInventory(this, 2, getSizeInventory());
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.isActive())
        {
            int cost = getFortronCost() + MFFSSettings.CAPACITOR_POWER_DRAIN;
            if (cost > 0)
            {
                requestFortron(cost, true);
            }

            //TODO: Change the draining to remove X% of transfered fortron.
            if (this.ticks % 10 == 0)
            { //cannot run if there is 0 energy!
                Set<IFortronFrequency> connected = new HashSet<>();
                for (ItemStack stack : getCards())
                {
                    if (stack == null)
                    {
                        continue;
                    }

                    if (stack.getItem() instanceof ICardInfinite)
                    {
                        setFortronEnergy(getFortronCapacity());
                    }
                    else if (stack.getItem() instanceof ICoordLink)
                    {
                        Location link = ((ICoordLink) stack.getItem()).getLink(stack);
                        if (link != null)
                        {
                            TileEntity link_machine = link.getTileEntity(this.worldObj);
                            if (link_machine instanceof IFortronFrequency)
                            {
                                connected.add(this);
                                connected.add((IFortronFrequency) link_machine);
                            }
                        }
                    }
                }
                if (connected.isEmpty())
                {
                    getLinkedDevices(connected);
                }

                FortronHelper.transfer(this, connected, mode, getTransmissionRate());
            }
        }
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
            return stack.getItem() instanceof ICardInfinite || stack.getItem() instanceof ItemCardLink;
        }
        else if (slot == 1)
        {
            return stack.getItem() instanceof ItemCardFrequency || stack.getItem() instanceof ItemCardLink;
        }
        return stack.getItem() instanceof IFieldModule;
    }

    /**
     * @return
     */
    @Override
    public Set<ItemStack> getCards()
    {
        Set<ItemStack> set = new HashSet<>();
        set.add(super.getCard());
        set.add(getStackInSlot(1));
        return set;
    }

    @Override
    public void getLinkedDevices(Set<IFortronFrequency> list)
    {
        list.addAll(FrequencyGrid.instance().getFortronTilesExcluding(this, new Vector3D(this), getTransmissionRange(), getFrequency()));
    }

    @Override
    public int getTransmissionRange()
    {
        return 15 + getModuleCount(ItemModuleScale.class);
    }

    @Override
    public int getTransmissionRate()
    {
        return 250 + 50 * getModuleCount(ItemModuleSpeed.class);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("transferMode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.mode = TransferMode.values()[nbt.getByte("transferMode")];
    }

    @Override
    public int getSizeInventory()
    {
        return 5;
    }

    public TransferMode getTransferMode()
    {
        return this.mode;
    }

    @Override
    public float getAmplifier()
    {
        return .001F;
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    @Override
    public IMessage handleMessage(IMessage imessage)
    {
        if (imessage instanceof EntityToggle)
        {
            EntityToggle tog = (EntityToggle) imessage;
            if (tog.toggle_opcode == EntityToggle.TRANSFER_TOGGLE)
            {
                this.mode = this.mode.toggle();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return null;
            }
        }
        return super.handleMessage(imessage);
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entityPlayer)
    {
        List<ItemStack> stack = super.getRemovedItems(entityPlayer);
        stack.add(new ItemStack(MFFS.fortronCapacitor));
        return stack;
    }
}
