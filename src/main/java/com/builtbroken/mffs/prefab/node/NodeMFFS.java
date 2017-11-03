package com.builtbroken.mffs.prefab.node;

import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mffs.MFFS;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Prefab for all MFFS nodes
 *
 * @author DarkCow
 */
public abstract class NodeMFFS extends TileNode implements IRotation
{
    /* If this machine is on */
    private boolean enabled;
    private boolean prevEnabled;

    /* If this tile requires a restone signal */
    private boolean redstone;
    private boolean prevRedstone;

    public NodeMFFS(String id)
    {
        super(id, MFFS.DOMAIN);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        if(ticks % 3 == 0)
        {
            redstone = getHost().isRedstonePowered();
        }
        if (prevEnabled != enabled)
        {
            onActivationChanged();
        }
        if (prevRedstone != redstone)
        {
            onRedstoneChanged();
        }
        prevRedstone = redstone;
        prevEnabled = enabled;
    }

    protected void onActivationChanged()
    {

    }

    protected void onRedstoneChanged()
    {

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setBoolean("enabled", enabled);
        return nbt;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        enabled = nbt.getBoolean("enabled");
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean on)
    {
        this.enabled = on;
    }

    @Override
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(getHost().getHostMeta());
    }
}
