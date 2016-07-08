package com.mffs.common;

import com.mffs.api.IActivatable;
import com.mffs.common.net.packet.EntityToggle;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Calclavia
 */
public abstract class TileMFFS extends TileEntity implements IActivatable {
    public float animation;

    /* Ticks */
    protected int ticks;

    /* If this machine is on */
    private boolean isActivated;

    /* If this tile requires a restone signal */
    private boolean isProvidingSignal;

    @Override
    public void updateEntity() {
        if (ticks++ == 0) {
            start();
        } else if (ticks >= Integer.MAX_VALUE) {
            ticks = 1;
        }
    }

    /* Starts the entity */
    public void start() {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("mffs_isActive", isActivated);
        nbt.setBoolean("mffs_redstone", isProvidingSignal);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        isActivated = nbt.getBoolean("mffs_isActive");
        isProvidingSignal = nbt.getBoolean("mffs_redstone");
    }

    @Override
    public boolean isActive() {
        return isActivated;
    }

    @Override
    public void setActive(boolean on) {
        if (!on && (isProvidingSignal || worldObj.isRemote)) {
            return;
        }
        this.isActivated = on;
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    public IMessage handleMessage(IMessage imessage) {
        if (imessage instanceof EntityToggle) {
            EntityToggle tog = (EntityToggle) imessage;
            if (tog.toggle_opcode == EntityToggle.REDSTONE_TOGGLE) {
                this.isProvidingSignal = !this.isProvidingSignal;
                this.isActivated = this.isProvidingSignal;
            } else if (tog.toggle_opcode == EntityToggle.TOGGLE_STATE) {
                this.isActivated = !this.isActivated;
            }
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        return null;
    }

    public ForgeDirection getDirection() {
        return ForgeDirection.getOrientation(getBlockMetadata());
    }

    /**
     * Overriden in a sign to provide the text.
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }

    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        //this.isActivated = pkt.func_148857_g().getBoolean("active");
        //this.isProvidingSignal = pkt.func_148857_g().getBoolean("redstone");
        readFromNBT(pkt.func_148857_g());
        super.onDataPacket(net, pkt);
    }
}
