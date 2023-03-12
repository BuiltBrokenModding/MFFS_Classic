package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;

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
    public void beforeSelect(Projector projector, Collection<? extends BlockPos> field) {}

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
    public boolean onCollideWithForceField(Level level, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onCalculate(Projector projector, Collection<BlockPos> fieldDefinition) {}
}
