package com.mffs.common.entity;

import com.mffs.api.SecurityClearance;
import com.mffs.api.inter.ISwitchable;
import com.mffs.api.inter.IWrench;
import mekanism.api.IMekWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.Random;

/**
 * Created by pwaln on 5/30/2016.
 */
public abstract class EntityMachine extends TileEntity implements ISidedInventory, IWrench, ISwitchable, IMekWrench {

    /* Deteremines if this machine is on */
    protected boolean isOn;

    /* Deteremines what side this machine is facing. */
    protected int side;

    /* The amount of ticks remaining on this machine */
    protected short ticks;

    /* If this machine is initialized */
    protected boolean init;

    /* The name of this machine */
    protected String name;

    /* The id of this machine */
    protected int id;

    /* The mode of this machine */
    protected short switchMode;

    /* If this machine is switching the value */
    protected boolean switchValue;

    /* The random seed of this machine */
    protected Random random = new Random();

    /* The chunk associated with this chunk */
    protected ForgeChunkManager.Ticket chunkTicket;

    /**
     * Constructor.
     */
    public EntityMachine() {
        this.isOn = false;
        this.switchValue = false;
        this.init = true;
        this.side = -1;
        this.switchMode = 0;
        this.id = 0;
        this.name = "Unknown";
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.isOn = tag.getBoolean("active");
        this.side = tag.getInteger("side");
        this.name = tag.getString("name");
        this.id = tag.getInteger("id");
        this.switchMode = tag.getShort("mode");
        this.switchValue = tag.getBoolean("value");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("active", this.isOn);
        tag.setInteger("side", this.side);
        tag.setString("name", this.name);
        tag.setInteger("id", this.id);
        tag.setShort("mode", this.switchMode);
        tag.setBoolean("value", this.switchValue);
    }

    @Override
    public boolean canUseWrench(EntityPlayer player, int x, int y, int z) {
        if(this.worldObj.isRemote) {
            return false;
        }
        return canAccess(player.getGameProfile().getName(), this, player, SecurityClearance.EB);
    }

    @Override
    public int getSide() {
        return side;
    }

    @Override
    public void setSide(int paramInt) {
        this.side = paramInt;
        //TODO: send packet
    }

    @Override
    public boolean wrenchCanManipulate(EntityPlayer paramEntityPlayer, int paramInt) {
        if(this.worldObj.isRemote) {
            return false;
        }
        return canAccess(paramEntityPlayer.getGameProfile().getName(), this, paramEntityPlayer, SecurityClearance.EB);
    }

    @Override
    public boolean isSwitchable() {
        return switchMode == 2;
    }

    @Override
    public void toggelSwitchValue() {
        this.switchValue = !this.switchValue;
        //TODO: send packet
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     *
     * @param p_70304_1_
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        return null;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     *
     * @param entity
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer entity) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                entity.getDistance(entity.posX + .5D, entity.posY + .5D, entity.posZ + .5D) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    public abstract boolean canAccess(String name, TileEntity tile, EntityPlayer player, SecurityClearance right);
    //public abstract EntityAdvSecurityStation getLinkedSecurityStation();
}
