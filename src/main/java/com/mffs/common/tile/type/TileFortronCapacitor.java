package com.mffs.common.tile.type;

import com.mffs.MFFS;
import com.mffs.api.TransferMode;
import com.mffs.api.card.ICard;
import com.mffs.api.card.ICardInfinite;
import com.mffs.api.card.ICoordLink;
import com.mffs.api.fortron.FrequencyGrid;
import com.mffs.api.fortron.IFortronCapacitor;
import com.mffs.api.fortron.IFortronFrequency;
import com.mffs.api.modules.IModule;
import com.mffs.api.utils.FortronHelper;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.upgrades.ModuleScale;
import com.mffs.common.items.modules.upgrades.ModuleSpeed;
import com.mffs.common.net.packet.ChangeTransferMode;
import com.mffs.common.net.packet.EntityToggle;
import com.mffs.common.tile.TileModuleAcceptor;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import mekanism.api.Coord4D;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class TileFortronCapacitor extends TileModuleAcceptor implements IFortronCapacitor {

    /* Current distribution method */
    private TransferMode mode = TransferMode.EQUALIZE;

    /**
     * Constructor.
     */
    public TileFortronCapacitor() {
        this.capacityBase = 700;
        this.capacityBoost = 10;
        this.module_index = 2;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        int cost = getFortronCost();
        if (cost > 0)
            requestFortron(cost, true);

        if (isActive() && this.ticks % 10 == 0) {
            Set<IFortronFrequency> connected = new HashSet<>();
            for (ItemStack stack : getCards()) {
                if (stack == null)
                    continue;

                if (stack.getItem() instanceof ICardInfinite) {
                    setFortronEnergy(getFortronCapacity());
                } else if (stack.getItem() instanceof ICoordLink) {
                    Coord4D link = ((ICoordLink) stack.getItem()).getLink(stack);
                    TileEntity link_machine = link.getTileEntity(worldObj);
                    if (link != null && link_machine instanceof IFortronFrequency) {
                        connected.add(this);
                        connected.add((IFortronFrequency) link_machine);
                    }
                }
            }
            if (connected.isEmpty())
                getLinkedDevices(connected);

            FortronHelper.transfer(this, connected, mode, getTransmissionRate());
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
        if (slot <= 1)
            return stack.getItem() instanceof ICard;
        return stack.getItem() instanceof IModule;
    }

    /**
     * @return
     */
    @Override
    public Set<ItemStack> getCards() {
        Set<ItemStack> set = new HashSet<>();
        set.add(super.getCard());
        set.add(getStackInSlot(1));
        return set;
    }

    @Override
    public void getLinkedDevices(Set<IFortronFrequency> list) {
        list.addAll(FrequencyGrid.instance().getFortronTiles(worldObj, new Vector3D(this), getTransmissionRange(), getFrequency()));
    }

    @Override
    public int getTransmissionRange() {
        return 15 + getModuleCount(ModuleScale.class);
    }

    @Override
    public int getTransmissionRate() {
        return 250 + 50 * getModuleCount(ModuleSpeed.class);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("transferMode", (byte) mode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mode = TransferMode.values()[nbt.getByte("transferMode")];
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    public TransferMode getTransferMode() {
        return this.mode;
    }

    @Override
    public float getAmplifier() {
        return .001F;
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    @Override
    public IMessage handleMessage(IMessage imessage) {
        if (imessage instanceof ChangeTransferMode) {
            this.mode = TransferMode.values()[((ChangeTransferMode) imessage).getToggle()];
            return null;
        } else if (imessage instanceof EntityToggle) {
            EntityToggle tog = (EntityToggle) imessage;
            if (tog.toggle_opcode == EntityToggle.TRANSFER_TOGGLE) {
                this.mode = this.mode.toggle();
                MFFS.channel.sendToAll(new ChangeTransferMode(this));
                return null;
            }
        }
        return super.handleMessage(imessage);
    }
}
