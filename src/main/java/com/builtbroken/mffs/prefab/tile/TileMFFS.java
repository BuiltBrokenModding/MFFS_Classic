package com.builtbroken.mffs.prefab.tile;

import com.builtbroken.mc.api.IModObject;
import com.builtbroken.mc.api.abstraction.world.IWorld;
import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.api.tile.IPlayerUsing;
import com.builtbroken.mc.api.tile.IRemovable;
import com.builtbroken.mc.api.tile.ITile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mffs.MFFS;
import com.builtbroken.mffs.api.IActivatable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Calclavia
 */
@Deprecated //Has been converted to node framework
public abstract class TileMFFS extends TileEntity implements IActivatable, IPacketIDReceiver, IRemovable.ICustomRemoval, IModObject, ITile, IPlayerUsing
{
    public static int PACKET_DESC_ID = -1;
    public static int PACKET_ACTIVATE_ID = 0;
    public static int PACKET_GUI_ID = 1;

    public float animation; //TODO ?

    /** Ticks */
    protected int ticks;

    protected boolean doDescPacket = false;

    /** If this machine is on */
    private boolean isActivated;

    /** If this tile requires a restone signal */
    private boolean isProvidingSignal;

    private List<EntityPlayer> playersUsingGUI = new ArrayList();

    private IWorld _worldCache;

    @Override
    public String getMod()
    {
        return MFFS.DOMAIN;
    }

    @Override
    public Collection<EntityPlayer> getPlayersUsing()
    {
        return playersUsingGUI;
    }

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

        if (isServer())
        {
            if (doDescPacket)
            {
                doDescPacket = false;
                sendDescPacket();
            }
            if (playersUsingGUI.size() > 0 && ticks % 3 == 0)
            {
                doGUIPacket();
            }
        }
    }

    /* Starts the entity */
    public void start()
    {
    }

    protected void doGUIPacket()
    {
        Iterator<EntityPlayer> it = playersUsingGUI.iterator();
        while (it.hasNext())
        {
            EntityPlayer player = it.next();
            if (player instanceof EntityPlayerMP && isValidGuiUser(player))
            {
                PacketTile packet = new PacketTile(this, PACKET_GUI_ID);
                packet.addWriter(byteBuf -> writeGuiPacket(byteBuf, player));
                Engine.packetHandler.sendToPlayer(packet, (EntityPlayerMP) player);
            }
            else
            {
                it.remove();
            }
        }
    }

    protected boolean isValidGuiUser(EntityPlayer player)
    {
        return player.inventoryContainer instanceof ContainerBase;
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
        doDescPacket = true;
    }

    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(getBlockMetadata());
    }

    @Override
    public final Packet getDescriptionPacket()
    {
        return Engine.packetHandler.toMCPacket(getDescPacket());
    }

    public IPacket getDescPacket()
    {
        PacketTile packetTile = new PacketTile(this, PACKET_DESC_ID);
        packetTile.addWriter(byteBuf -> writeDescPacket(byteBuf));
        return packetTile;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient())
        {
            if (id == PACKET_DESC_ID)
            {
                readDescPacket(buf);
                return true;
            }
            else if (id == PACKET_GUI_ID)
            {
                readGuiPacket(buf, player);
                return true;
            }
        }
        else
        {
            if (id == PACKET_ACTIVATE_ID)
            {
                //TODO add anti-cheat check to ensure player is within range of tile
                setActive(buf.readBoolean());
                return true;
            }
        }
        return false;
    }

    public void sendActivationStateToServer()
    {
        if (isClient())
        {
            Engine.packetHandler.sendToServer(new PacketTile(this, PACKET_ACTIVATE_ID, isActive()));
        }
    }

    public void sendDescPacket()
    {
        PacketTile tile = new PacketTile(this);
        tile.addWriter(byteBuf -> writeDescPacket(byteBuf));
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

    public void writeGuiPacket(ByteBuf buf, EntityPlayer player)
    {

    }

    public void readGuiPacket(ByteBuf buf, EntityPlayer player)
    {

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
    public IWorld world()
    {
        if (_worldCache == null && worldObj != null)
        {
            _worldCache = Engine.getWorld(worldObj.provider.dimensionId);
        }
        return _worldCache;
    }
}
