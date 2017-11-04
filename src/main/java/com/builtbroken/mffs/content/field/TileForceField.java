package com.builtbroken.mffs.content.field;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.content.projector.TileForceFieldProjector;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Calclavia, DarkCow
 */
public final class TileForceField extends TileEntity //TODO find a way to remove the need for a tile entity
{
    public static final String NBT_PROJECTOR_POSITION = "projectorPosition";
    public static final String NBT_CAMOUFLAGE = "camouflage";

    /** Material to render in place of the default block */
    public ItemStack camouflageMaterial;

    /* Location of the projector */
    private BlockPos projectorPosition;

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
        if (this.camouflageMaterial != null)
        {
            tag.setTag(NBT_CAMOUFLAGE, camouflageMaterial.writeToNBT(new NBTTagCompound()));
        }
        if (projectorPosition != null)
        {
            tag.setTag(NBT_PROJECTOR_POSITION, projectorPosition.save());
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
        if (pkt.func_148857_g().hasKey(NBT_CAMOUFLAGE))
        {
            camouflageMaterial = ItemStack.loadItemStackFromNBT(pkt.func_148857_g().getCompoundTag(NBT_CAMOUFLAGE));
        }
        if (pkt.func_148857_g().hasKey(NBT_PROJECTOR_POSITION))
        {
            projectorPosition = new BlockPos(pkt.func_148857_g().getCompoundTag(NBT_PROJECTOR_POSITION));
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (projectorPosition != null)
        {
            nbt.setTag(NBT_PROJECTOR_POSITION, projectorPosition.save());
        }
        if (camouflageMaterial != null)
        {
            nbt.setTag(NBT_CAMOUFLAGE, camouflageMaterial.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey(NBT_PROJECTOR_POSITION))
        {
            projectorPosition = new BlockPos(nbt.getCompoundTag(NBT_PROJECTOR_POSITION));
        }
        if (nbt.hasKey(NBT_CAMOUFLAGE))
        {
            camouflageMaterial = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(NBT_CAMOUFLAGE));
        }
    }

    /**
     * Set the location of the projector that projected this block.
     *
     * @param vec
     */
    public void setProjectorPosition(IPos3D vec)
    {
        this.projectorPosition = vec instanceof BlockPos ? (BlockPos) vec : new BlockPos(vec);
    }

    public IPos3D getProjectorPosition()
    {
        return projectorPosition;
    }

    /**
     * This gets the projector and calls a block update.
     *
     * @return
     */
    public TileForceFieldProjector getProjector()
    {
        if (this.projectorPosition != null)
        {
            //TODO check if chunk is loaded first
            TileEntity entity = projectorPosition.getTileEntity(getWorldObj());
            if (entity != null && entity instanceof TileForceFieldProjector)
            {
                if (worldObj.isRemote || ((TileForceFieldProjector) entity).getCalculatedField().contains(new Vector3D(this))) //TODO change to block pos
                {
                    return (TileForceFieldProjector) entity;
                }
            }
        }
        return null;
    }

    public boolean shouldDestroy()
    {
        return getProjector() == null;  //TODO check if chunk is loaded first, don't destroy if chunk doesn't exist to avoid lag
    }
}
