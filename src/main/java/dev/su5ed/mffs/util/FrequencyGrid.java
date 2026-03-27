package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import one.util.streamex.StreamEx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tracks all FortronStorage instances by frequency across the game.
 * 1.12.2 Backport: EffectiveSide → FMLCommonHandler.instance().getEffectiveSide(),
 * isRemoved() → isInvalid(), getLevel() → getWorld(), closerThan() → manual distance check.
 */
public class FrequencyGrid {
    private static FrequencyGrid CLIENT_INSTANCE = new FrequencyGrid();
    private static FrequencyGrid SERVER_INSTANCE = new FrequencyGrid();

    private final Object lock = new Object();
    private final Set<FortronStorage> frequencyGrid = new HashSet<>();

    public <T extends FortronStorage> void register(T fortron) {
        synchronized (this.lock) {
            BlockPos pos = fortron.getOwner().getPos();
            this.frequencyGrid.removeIf(frequency -> {
                TileEntity owner = frequency.getOwner();
                return frequency == null || owner.isInvalid() || owner.getPos().equals(pos);
            });
            this.frequencyGrid.add(fortron);
        }
    }

    public static FrequencyGrid instance() {
        return instance(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT);
    }

    public static FrequencyGrid instance(boolean client) {
        return client ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public void unregister(FortronStorage tileEntity) {
        synchronized (this.lock) {
            this.frequencyGrid.remove(tileEntity);
            cleanUpLocked();
        }
    }

    public Set<FortronStorage> get() {
        synchronized (this.lock) {
            return new HashSet<>(this.frequencyGrid);
        }
    }

    public Set<FortronStorage> get(int frequency) {
        return StreamEx.of(get())
            .filter(fortron -> fortron != null && !fortron.getOwner().isInvalid() && fortron.getFrequency() == frequency)
            .toSet();
    }

    public List<FortronStorage> get(World world, Vec3i position, int radius, int frequency) {
        return StreamEx.of(get(frequency))
            .filter(fortron -> {
                TileEntity owner = fortron.getOwner();
                return owner.getWorld() == world && owner.getPos().distanceSq(position.getX(), position.getY(), position.getZ()) <= (double) radius * radius;
            })
            .toList();
    }

    public void cleanUp() {
        synchronized (this.lock) {
            cleanUpLocked();
        }
    }

    private void cleanUpLocked() {
        this.frequencyGrid.removeIf(fortron -> fortron == null || fortron.getOwner().isInvalid());
    }

    /**
     * Called to re-initiate the grid. Used when server restarts or when player rejoins a world to
     * clean up previously registered objects.
     */
    public static void reinitiate() {
        CLIENT_INSTANCE = new FrequencyGrid();
        SERVER_INSTANCE = new FrequencyGrid();
    }
}
