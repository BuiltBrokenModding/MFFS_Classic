package com.builtbroken.mffs;

import com.builtbroken.mc.framework.mod.AbstractProxy;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.card.id.ContainerCardID;
import com.builtbroken.mffs.content.biometric.BiometricContainer;
import com.builtbroken.mffs.content.biometric.TileBiometricIdentifier;
import com.builtbroken.mffs.content.cap.FortronCapacitorContainer;
import com.builtbroken.mffs.content.cap.TileFortronCapacitor;
import com.builtbroken.mffs.content.gen.ContainerCoercionDeriver;
import com.builtbroken.mffs.content.gen.TileCoercionDeriver;
import com.builtbroken.mffs.content.interdiction.InterdictionContainer;
import com.builtbroken.mffs.content.interdiction.TileInterdictionMatrix;
import com.builtbroken.mffs.content.projector.ForceFieldProjectorContainer;
import com.builtbroken.mffs.content.projector.TileForceFieldProjector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by pwaln on 5/22/2016.
 */
public class CommonProxy extends AbstractProxy
{
    /**
     * Called before the main INITIALIZE.
     */
    @Override
    public void preInit()
    {
    }

    /**
     * Called along with the main Initialize.
     */
    @Override
    public void init()
    {
    }

    /**
     * Called after the main Init.
     */
    @Override
    public void postInit()
    {

    }

    /**
     * Returns a Server side Container to be displayed to the user.
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null)
            {
                if (tileEntity instanceof TileCoercionDeriver)
                {
                    return new ContainerCoercionDeriver(player, (TileCoercionDeriver) tileEntity);
                }
                else if (tileEntity instanceof TileForceFieldProjector)
                {
                    return new ForceFieldProjectorContainer(player, (TileForceFieldProjector) tileEntity);
                }
                else if (tileEntity instanceof TileFortronCapacitor)
                {
                    return new FortronCapacitorContainer(player, (TileFortronCapacitor) tileEntity);
                }
                else if (tileEntity instanceof TileBiometricIdentifier)
                {
                    return new BiometricContainer(player, (TileBiometricIdentifier) tileEntity);
                }
                else if (tileEntity instanceof TileInterdictionMatrix)
                {
                    return new InterdictionContainer(player, (TileInterdictionMatrix) tileEntity);
                }
            }
        }
        else if (ID == 1)
        {
            return new ContainerCardID(player, player.inventory, x);
        }
        return null;
    }

    /**
     * Registers a beam Effect. Client Side ONLY.
     *
     * @param world    The world the beam occurs.
     * @param origin   The origin of the beam.
     * @param dest     The destination of the beam.
     * @param r        The red hue.
     * @param g        The green hue.
     * @param b        The blue hue.
     * @param lifespan How long the particles should last.
     */
    public void registerBeamEffect(World world, Vector3D origin, Vector3D dest, float r, float g, float b, int lifespan)
    {
    }

    /**
     * Animates a portion of Fortron.
     *
     * @param world  The world the beam occurs.
     * @param origin The origin of the beam.
     * @param r      The red hue.
     * @param g      The green hue.
     * @param b      The blue hue.
     * @param life   How long the particles should last.
     */
    public void animateFortron(World world, Vector3D origin, float r, float g, float b, int life)
    {
    }

    /**
     * Returns a Container to be displayed to the user. On the client side, this
     * needs to return a instance of GuiScreen On the server side, this needs to
     * return a instance of Container
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}
