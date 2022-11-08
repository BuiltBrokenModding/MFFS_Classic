package dev.su5ed.mffs.api.fortron;

import dev.su5ed.mffs.api.FrequencyBlock;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import one.util.streamex.StreamEx;

import java.util.HashSet;
import java.util.Set;

/**
 * A grid MFFS uses to search for machines with frequencies that can be linked and spread Fortron
 * energy.
 *
 * @author Calclavia
 */
public class FrequencyGrid {
    private static FrequencyGrid CLIENT_INSTANCE = new FrequencyGrid();
    private static FrequencyGrid SERVER_INSTANCE = new FrequencyGrid();

    private final Set<FrequencyBlock> frequencyGrid = new HashSet<>();

    public <T extends BlockEntity & FrequencyBlock> void register(T be) {
		this.frequencyGrid.removeIf(frequency -> frequency == null || ((BlockEntity) frequency).isRemoved() || ((BlockEntity) frequency).getBlockPos().equals(be.getBlockPos()));
        this.frequencyGrid.add(be);
    }

    public static FrequencyGrid instance() {
        return EffectiveSide.get().isServer() ? SERVER_INSTANCE : CLIENT_INSTANCE;
    }

    public void unregister(FrequencyBlock tileEntity) {
        this.frequencyGrid.remove(tileEntity);
        cleanUp();
    }

    public Set<FrequencyBlock> get() {
        return this.frequencyGrid;
    }

    /**
     * Gets a list of TileEntities that has a specific frequency.
     *
     * @param frequency - The Frequency
     */
    public Set<FrequencyBlock> get(int frequency) {
        return StreamEx.of(get())
			.filter(block -> block != null && !((BlockEntity) block).isRemoved() && block.getFrequency() == frequency)
			.toSet();
    }

	public void cleanUp() {
        this.frequencyGrid.removeIf(block -> block == null || ((BlockEntity) block).isRemoved() || ((BlockEntity) block).getLevel().getBlockEntity(((BlockEntity) block).getBlockPos()) != block);
    }

    public Set<FrequencyBlock> get(Level level, Vec3i position, int radius, int frequency) {
        return StreamEx.of(get(frequency))
            .filter(block -> ((BlockEntity) block).getLevel() == level && position.closerThan(((BlockEntity) block).getBlockPos(), radius))
            .toSet();
    }

    public Set<FortronFrequency> getFortronTiles(Level level) {
        return StreamEx.of(get())
			.filter(block -> ((BlockEntity) block).getLevel() == level && block instanceof FortronFrequency)
			.map(block -> (FortronFrequency) block)
			.toSet();
    }

    public Set<? extends FortronFrequency> getFortronTiles(Level level, Vec3i position, int radius, int frequency) {
        return StreamEx.of(get(frequency))
            .select(FortronFrequency.class)
			.filter(block -> ((BlockEntity) block).getLevel() == level && ((BlockEntity) block).getBlockPos().closerThan(position, radius))
			.toSet();
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
