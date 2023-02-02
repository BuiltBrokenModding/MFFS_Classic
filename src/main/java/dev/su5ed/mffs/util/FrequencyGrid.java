package dev.su5ed.mffs.util;

import dev.su5ed.mffs.api.fortron.FortronStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import one.util.streamex.StreamEx;

import java.util.HashSet;
import java.util.Set;

public class FrequencyGrid {
    private static FrequencyGrid CLIENT_INSTANCE = new FrequencyGrid();
    private static FrequencyGrid SERVER_INSTANCE = new FrequencyGrid();

    private final Set<FortronStorage> frequencyGrid = new HashSet<>();

    public <T extends FortronStorage> void register(T fortron) {
        BlockPos pos = fortron.getOwner().getBlockPos();
        this.frequencyGrid.removeIf(frequency -> {
            BlockEntity owner = frequency.getOwner();
            return frequency == null || owner.isRemoved() || owner.getBlockPos().equals(pos);
        });
        this.frequencyGrid.add(fortron);
    }

    public static FrequencyGrid instance() {
        return EffectiveSide.get().isServer() ? SERVER_INSTANCE : CLIENT_INSTANCE;
    }

    public void unregister(FortronStorage tileEntity) {
        this.frequencyGrid.remove(tileEntity);
        cleanUp();
    }

    public Set<FortronStorage> get() {
        return this.frequencyGrid;
    }

    public Set<FortronStorage> get(int frequency) {
        return StreamEx.of(get())
            .filter(fortron -> fortron != null && !fortron.getOwner().isRemoved() && fortron.getFrequency() == frequency)
            .toSet();
    }

    public void cleanUp() {
        this.frequencyGrid.removeIf(fortron -> fortron == null || fortron.getOwner().isRemoved());
    }

    public Set<FortronStorage> get(Level level, Vec3i position, int radius, int frequency) {
        return StreamEx.of(get(frequency))
            .filter(fortron -> {
                BlockEntity owner = fortron.getOwner();
                return owner.getLevel() == level && position.closerThan(owner.getBlockPos(), radius);
            })
            .toSet();
    }

    public Set<? extends FortronStorage> getFortronBlocks(Level level, Vec3i position, int radius, int frequency) {
        return StreamEx.of(get(frequency))
            .filter(fortron -> {
                BlockEntity owner = fortron.getOwner();
                return owner.getLevel() == level && owner.getBlockPos().closerThan(position, radius);
            })
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
