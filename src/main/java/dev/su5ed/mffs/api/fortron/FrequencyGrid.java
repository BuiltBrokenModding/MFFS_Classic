package dev.su5ed.mffs.api.fortron;

import dev.su5ed.mffs.api.FrequencyBlock;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void unregister(FrequencyBlock tileEntity) {
        this.frequencyGrid.remove(tileEntity);
        this.cleanUp();
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
        return get().stream()
			.filter(block -> block != null && !((BlockEntity) block).isRemoved() && block.getFrequency() == frequency)
			.collect(Collectors.toSet());
    }

	public void cleanUp() {
		this.frequencyGrid.stream()
			.filter(block -> block == null || ((BlockEntity) block).isRemoved() || ((BlockEntity) block).getLevel().getBlockEntity(((BlockEntity) block).getBlockPos()) != block)
			.forEach(this::unregister);
    }

    public Set<FrequencyBlock> get(Level level, Vec3i position, int radius, int frequency) {
        Set<FrequencyBlock> set = new HashSet<>();

        for (FrequencyBlock be : get(frequency)) {
            if (((BlockEntity) be).getLevel() == level) {
                if (position.closerThan(((BlockEntity) be).getBlockPos(), radius)) {
                    set.add(be);
                }
            }
        }
        return set;

    }

    public Set<FortronFrequency> getFortronTiles(Level level) {
        return get().stream()
			.filter(block -> ((BlockEntity) block).getLevel() == level && block instanceof FortronFrequency)
			.map(block -> (FortronFrequency) block)
			.collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked") // I checked it for you, java, it's okay
    public <T extends BlockEntity & FortronFrequency> Set<T> getFortronTiles(Level level, Vec3i position, int radius, int frequency) {
        return get(frequency).stream()
			.filter(block -> ((BlockEntity) block).getLevel() == level && block instanceof FortronFrequency
				&& ((BlockEntity) block).getBlockPos().closerThan(position, radius))
			.map(block -> (T) block)
			.collect(Collectors.toSet());
    }

    /**
     * Called to re-initiate the grid. Used when server restarts or when player rejoins a world to
     * clean up previously registered objects.
     */
    public static void reinitiate() {
        CLIENT_INSTANCE = new FrequencyGrid();
        SERVER_INSTANCE = new FrequencyGrid();
    }

    public static FrequencyGrid instance() {
        return EffectiveSide.get().isServer() ? SERVER_INSTANCE : CLIENT_INSTANCE;
    }
}
