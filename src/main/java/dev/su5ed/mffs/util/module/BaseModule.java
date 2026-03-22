package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

/**
 * Base implementation of {@link Module} with no-op defaults.
 * 1.12.2 Backport: Level→World, Entity namespace changed.
 */
public class BaseModule implements Module {
    protected final ModuleType<?> type;
    protected final ItemStack stack;

    public BaseModule(ModuleType<?> type, ItemStack stack) {
        this.type = type;
        this.stack = stack;
    }

    @Override
    public ModuleType<?> getType() {
        return this.type;
    }

    @Override
    public void beforeSelect(Projector projector, Collection<? extends TargetPosPair> field) {}

    @Override
    public void beforeProject(Projector projector) {}

    @Override
    public ProjectAction onSelect(Projector projector, BlockPos pos) {
        return ProjectAction.PROJECT;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos pos) {
        return ProjectAction.PROJECT;
    }

    @Override
    public boolean onCollideWithForceField(World world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onCalculate(Projector projector, Collection<TargetPosPair> fieldDefinition) {}
}
