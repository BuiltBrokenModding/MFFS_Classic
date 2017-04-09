package com.mffs.api.utils;

import com.mffs.api.IBlockFrequency;
import com.mffs.api.fortron.FrequencyGrid;
import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.api.security.Permission;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.interdiction.ItemModuleBlockAccess;
import com.mffs.common.items.modules.interdiction.ItemModuleBlockAlter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

/**
 * Created by pwaln on 7/9/2016.
 */
public class MatrixHelper
{

    /**
     * Find the matrix within distance.
     *
     * @param world
     * @param vec
     * @param seq
     * @return
     */
    public static IInterdictionMatrix findMatrix(World world, Vector3D vec, Set<IBlockFrequency> seq)
    {
        for (IBlockFrequency freq : seq)
        {
            TileEntity entity = (TileEntity) freq;
            if (entity instanceof IInterdictionMatrix && world == entity.getWorldObj())
            {
                IInterdictionMatrix matrix = (IInterdictionMatrix) freq;
                if (matrix.isActive() && vec.distance(entity.xCoord, entity.yCoord, entity.zCoord) < matrix.getActionRange())
                {
                    return matrix;
                }
            }
        }
        return null;
    }

    /**
     * Find the matrix within distance.
     *
     * @param world
     * @param vec
     * @return
     */
    public static IInterdictionMatrix findMatrix(World world, Vector3D vec)
    {
        for (IBlockFrequency freq : FrequencyGrid.instance().get())
        {
            TileEntity entity = (TileEntity) freq;
            if (entity instanceof IInterdictionMatrix && world == entity.getWorldObj())
            {
                IInterdictionMatrix matrix = (IInterdictionMatrix) freq;
                if (matrix.isActive() && vec.distance(entity.xCoord, entity.yCoord, entity.zCoord) < matrix.getActionRange())
                {
                    return matrix;
                }
            }
        }
        return null;
    }

    /**
     * Check if access is granted given a permission and a matrix.
     *
     * @param matrix
     * @param name
     * @param perm
     * @return
     */
    public static boolean hasPermission(IInterdictionMatrix matrix, String name, Permission perm)
    {
        IBiometricIdentifier bio = matrix.getBiometricIdentifier();
        if (bio != null && matrix.isActive())
        {
            return bio.isAccessGranted(name, perm);
        }
        return true;
    }

    /**
     * @param matrix
     * @param action
     * @param player
     * @return
     */
    public static boolean hasPermission(IInterdictionMatrix matrix, PlayerInteractEvent.Action action, EntityPlayer player)
    {
        boolean perm = true;
        if (matrix.getModuleCount(ItemModuleBlockAccess.class) > 0 && action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
        {
            perm = hasPermission(matrix, player.getGameProfile().getName(), Permission.BLOCK_ACCESS);
        }

        if (perm && matrix.getModuleCount(ItemModuleBlockAlter.class) > 0 && (action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || player.getCurrentEquippedItem() != null))
        {
            perm = hasPermission(matrix, player.getGameProfile().getName(), Permission.BLOCK_PLACE_ACCESS);
        }

        return perm;
    }
}
