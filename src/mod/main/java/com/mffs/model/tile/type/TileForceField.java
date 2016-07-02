package com.mffs.model.tile.type;

import com.mffs.api.vector.Vector3D;
import com.mffs.model.TileMFFS;
import com.mffs.model.items.modules.upgrades.ModuleCamouflage;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Calclavia
 */
public final class TileForceField extends TileMFFS {

    /* Represents the item that is this block */
    public ItemStack camo;

    /* Location of the projector */
    private Vector3D projector;

    /**
     * Determines if this TileEntity requires update calls.
     *
     * @return True if you want updateEntity() to be called, false if not
     */
    @Override
    public boolean canUpdate() {
        return false;
    }

    /**
     * Overriden in a sign to provide the text.
     */
    @Override
    public Packet getDescriptionPacket() {
        S35PacketUpdateTileEntity pkt = (S35PacketUpdateTileEntity) super.getDescriptionPacket();
        if (getProj() != null) {
            if (this.camo != null) {
                pkt.func_148857_g().setTag("camo", camo.writeToNBT(new NBTTagCompound()));
            }
            pkt.func_148857_g().setTag("proj", projector.writeToNBT(new NBTTagCompound()));
        }
        return pkt;
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
        if (pkt.func_148857_g().hasKey("camo")) {
            camo = ItemStack.loadItemStackFromNBT(pkt.func_148857_g().getCompoundTag("camo"));
        }
        NBTTagCompound tag = pkt.func_148857_g().getCompoundTag("proj");
        if (projector != null) {
            projector.x = tag.getDouble("x");
            projector.y = tag.getDouble("y");
            projector.z = tag.getDouble("z");
        } else {
            projector = new Vector3D(tag);
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        super.onDataPacket(net, pkt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("proj", projector.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        projector = new Vector3D(nbt.getCompoundTag("proj"));
    }

    /**
     * Set the location of the projector that projected this block.
     *
     * @param vec
     */
    public void setProjector(Vector3D vec) {
        this.projector = vec;
        if (!this.worldObj.isRemote)
            refresh();
    }

    /**
     * This gets the projector and calls a block update.
     *
     * @return
     */
    public TileForceFieldProjector getProj() {
        TileForceFieldProjector proj = findProj();
        if (proj != null)
            return proj;

        if (!worldObj.isRemote)
            worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
        return null;
    }

    /**
     * Gets the projector located at the given location.
     *
     * @return
     */
    private TileForceFieldProjector findProj() {
        if (this.projector != null) {
            TileEntity entity = projector.getTileEntity(getWorldObj());
            if (entity != null && entity instanceof TileForceFieldProjector) {
                if (worldObj.isRemote || ((TileForceFieldProjector) entity).getCalculatedField().contains(new Vector3D(this))) {
                    if (!worldObj.isRemote)
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

                    return (TileForceFieldProjector) entity;
                }
            }
        }
        return null;
    }

    /**
     * Refreshes all attributes of this entity.
     */
    private void refresh() {
        if (this.worldObj.isRemote)
            return;

        TileForceFieldProjector proj = getProj();
        if (proj != null) {
            if (proj.getModuleCount(ModuleCamouflage.class) > 0) {
                //TODO: CustomMode
                for (int slot : proj.getModuleSlots()) {//TODO: Add exclusive slot for camoflauge / disintigration blocks
                    ItemStack stack = proj.getStackInSlot(slot);
                    if (stack != null && stack.getItem() instanceof ItemBlock) {
                        this.camo = stack;
                        return;
                    }
                }
            }
        }
    }

}
