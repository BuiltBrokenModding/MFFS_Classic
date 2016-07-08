package com.mffs.api.fortron;

import com.mffs.api.IBlockFrequency;
import com.mffs.api.vector.Vector3D;
import cpw.mods.fml.common.FMLCommonHandler;
import mekanism.api.Pos3D;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.*;

/**
 * A grid MFFS uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 *
 * @author Calclavia
 */
public class FrequencyGrid {
    private static FrequencyGrid CLIENT_INSTANCE = new FrequencyGrid();
    private static FrequencyGrid SERVER_INSTANCE = new FrequencyGrid();

    private final Set<IBlockFrequency> frequencyGrid = Collections.newSetFromMap(new WeakHashMap<IBlockFrequency, Boolean>());

    /**
     * Called to re-initiate the grid. Used when server restarts or when player rejoins a world to
     * clean up previously registered objects.
     */
    public static void reinitiate() {
        CLIENT_INSTANCE = new FrequencyGrid();
        SERVER_INSTANCE = new FrequencyGrid();
    }

    public static FrequencyGrid instance() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient() ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public void register(IBlockFrequency tileEntity) {
        synchronized (frequencyGrid) {
            try {
                Iterator<IBlockFrequency> it = this.frequencyGrid.iterator();

                while (it.hasNext()) {
                    IBlockFrequency frequency = it.next();

                    if (frequency == null) {
                        it.remove();
                        continue;
                    }

                    if (((TileEntity) frequency).isInvalid()) {
                        it.remove();
                        continue;
                    }

                    if (new Pos3D((TileEntity) frequency).equals(new Pos3D((TileEntity) tileEntity))) {
                        it.remove();
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            frequencyGrid.add(tileEntity);
        }
    }

    public void unregister(IBlockFrequency tileEntity) {
        synchronized (frequencyGrid) {
            frequencyGrid.remove(tileEntity);
            cleanUp();
        }
    }

    public Set<IBlockFrequency> get() {
        return frequencyGrid;
    }

    /**
     * Gets a list of TileEntities that has a specific frequency.
     *
     * @param frequency - The Frequency
     */
    public Set<IBlockFrequency> get(int frequency) {
        Set<IBlockFrequency> set = new HashSet<IBlockFrequency>();

        for (IBlockFrequency tile : this.get()) {
            if (tile != null && !((TileEntity) tile).isInvalid()) {
                if (tile.getFrequency() == frequency) {
                    set.add(tile);
                }
            }
        }

        return set;
    }

    public void cleanUp() {
        Set<IBlockFrequency> tilesToRemove = new HashSet<IBlockFrequency>();
        Iterator<IBlockFrequency> it = this.frequencyGrid.iterator();

        while (it.hasNext()) {
            IBlockFrequency frequency = it.next();

            if (frequency == null) {
                tilesToRemove.add(frequency);
                continue;
            }

            if (((TileEntity) frequency).isInvalid()) {
                tilesToRemove.add(frequency);
                continue;
            }

            if (((TileEntity) frequency).getWorldObj().getTileEntity(((TileEntity) frequency).xCoord, ((TileEntity) frequency).yCoord, ((TileEntity) frequency).zCoord) != ((TileEntity) frequency)) {
                tilesToRemove.add(frequency);
                continue;
            }
        }

        tilesToRemove.forEach(tile -> FrequencyGrid.instance().unregister(tile));
    }

    public Set<IBlockFrequency> get(World world, Vector3D position, int radius, int frequency) {
        Set<IBlockFrequency> set = new HashSet<IBlockFrequency>();

        this.get(frequency).forEach(tileEntity -> {
            TileEntity tile = (TileEntity) tileEntity;
            if (tile.getWorldObj() == world) {
                if (position.distance(tile.xCoord, tile.yCoord, tile.zCoord) <= radius) {
                    set.add(tileEntity);
                }
            }
        });
        return set;

    }

    /**
     * Gets FortronFreqency entityes registered.
     *
     * @param world     The world.
     * @param position  The position
     * @param radius    The radius to check.
     * @param frequency The frequency to check.
     * @return
     */
    public Set<IFortronFrequency> getFortronTiles(World world, Vector3D position, int radius, int frequency) {
        Set<IFortronFrequency> set = new HashSet<>();

        this.get(frequency).forEach(entity -> {
            TileEntity tile = (TileEntity) entity;
            if (tile.getWorldObj() == world && entity instanceof IFortronFrequency) {
                if (position.distance(tile.xCoord, tile.yCoord, tile.zCoord) <= radius) {
                    set.add((IFortronFrequency) entity);
                }
            }
        });
        return set;

    }
}