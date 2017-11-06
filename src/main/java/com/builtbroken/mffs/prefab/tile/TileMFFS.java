package com.builtbroken.mffs.prefab.tile;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.tile.IRemovable;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mffs.api.IActivatable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Calclavia
 */
@Deprecated //Has been converted to node framework
public abstract class TileMFFS extends TileEntity implements IActivatable, IPacketIDReceiver, IRemovable.ICustomRemoval, IWorldPosition
{
    public static int PACKET_DESC_ID = -1;

    public float animation;

    /* Ticks */
    protected int ticks;

    /* If this machine is on */
    private boolean isActivated;

    /* If this tile requires a restone signal */
    private boolean isProvidingSignal;

    @Override
    public void updateEntity()
    {
        if (ticks++ == 0)
        {
            start();
        }
        else if (ticks >= Integer.MAX_VALUE)
        {
            ticks = 1;
        }
    }

    /* Starts the entity */
    public void start()
    {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("mffs_isActive", isActivated);
        nbt.setBoolean("mffs_redstone", isProvidingSignal);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        isActivated = nbt.getBoolean("mffs_isActive");
        isProvidingSignal = nbt.getBoolean("mffs_redstone");
    }

    @Override
    public boolean isActive()
    {
        return isActivated;
    }

    @Override
    public void setActive(boolean on)
    {
        this.isActivated = on;
    }

    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(getBlockMetadata());
    }

    /**
     * Overriden in a sign to provide the text.
     */
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (worldObj.isRemote && PACKET_DESC_ID == id)
        {
            readDescPacket(buf);
            return true;
        }
        return false;
    }

    public void sendDescPacket()
    {
        PacketTile tile = new PacketTile(this);
        writeDescPacket(tile.data());
        Engine.packetHandler.sendToAllAround(tile, this);
    }

    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeBoolean(isActivated);
        buf.writeBoolean(isProvidingSignal);
    }

    public void readDescPacket(ByteBuf buf)
    {
        isActivated = buf.readBoolean();
        isProvidingSignal = buf.readBoolean();
    }

    @Override
    public boolean canBeRemoved(EntityPlayer entityPlayer)
    {
        return WrenchUtility.isHoldingWrench(entityPlayer);
    }

    @Override
    public double x()
    {
        return xCoord;
    }

    @Override
    public double y()
    {
        return yCoord;
    }

    @Override
    public double z()
    {
        return zCoord;
    }

    @Override
    public World oldWorld()
    {
        return worldObj;
    }
}
