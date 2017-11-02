package com.builtbroken.mffs.common.tile.type;

import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.modules.projector.ItemModuleCamouflage;
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
public final class TileForceField extends TileEntity
{

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
    public boolean canUpdate()
    {
        return false;
    }

    /**
     * Overriden in a sign to provide the text.
     */
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        if (getProj() != null)
        {
            if (this.camo != null)
            {
                tag.setTag("camo", camo.writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("proj", projector.writeToNBT(new NBTTagCompound()));
        }
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
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        if (pkt.func_148857_g().hasKey("camo"))
        {
            camo = ItemStack.loadItemStackFromNBT(pkt.func_148857_g().getCompoundTag("camo"));
        }
        if (pkt.func_148857_g().hasKey("proj"))
        {
            NBTTagCompound tag = pkt.func_148857_g().getCompoundTag("proj");
            if (projector != null)
            {
                projector.x = tag.getDouble("x");
                projector.y = tag.getDouble("y");
                projector.z = tag.getDouble("z");
            }
            else
            {
                setProjector(new Vector3D(tag));
            }
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        super.onDataPacket(net, pkt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (getProj() != null)
        {
            nbt.setTag("proj", projector.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        projector = new Vector3D(nbt.getCompoundTag("proj"));
    }

    /**
     * Set the location of the projector that projected this block.
     *
     * @param vec
     */
    public void setProjector(Vector3D vec)
    {
        this.projector = vec;
        if (!this.worldObj.isRemote)
        {
            refresh();
        }
    }

    /**
     * This gets the projector and calls a block update.
     *
     * @return
     */
    public TileForceFieldProjector getProj()
    {
        TileForceFieldProjector proj = findProj();
        if (proj != null)
        {
            return proj;
        }

        if (!worldObj.isRemote)
        {
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }
        return null;
    }

    /**
     * Gets the projector located at the given location.
     *
     * @return
     */
    public TileForceFieldProjector findProj()
    {
        if (this.projector != null)
        {
            TileEntity entity = projector.getTileEntity(getWorldObj());
            if (entity != null && entity instanceof TileForceFieldProjector)
            {
                if (worldObj.isRemote || ((TileForceFieldProjector) entity).getCalculatedField().contains(new Vector3D(this)))
                {
                    return (TileForceFieldProjector) entity;
                }
            }
        }
        return null;
    }

    public Vector3D getProjLoc()
    {
        return this.projector;
    }

    /**
     * Refreshes all attributes of this entity.
     */
    private void refresh()
    {
        TileForceFieldProjector proj = findProj();
        if (proj != null)
        {
            if (proj.getModuleCount(ItemModuleCamouflage.class) > 0)
            {
                //TODO: CustomMode
                for (ItemStack stack : proj.getFilterStacks())
                {
                    if (stack != null && stack.getItem() instanceof ItemBlock)
                    {
                        this.camo = stack;
                        return;
                    }
                }
            }
        }
    }
}
