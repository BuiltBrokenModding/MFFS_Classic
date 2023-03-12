package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Set;

public class BaseModule implements Module {
    private final float fortronCost;
    private final Set<Category> categories;

    public BaseModule(Category... categories) {
        this(0.5F, categories);
    }

    public BaseModule(float fortronCost) {
        this(fortronCost, Category.MATRIX);
    }

    public BaseModule(float fortronCost, Category... categories) {
        this.fortronCost = fortronCost;
        this.categories = Set.of(categories);
    }

    @Override
    public float getFortronCost(float amplifier) {
        return this.fortronCost * amplifier;
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
    public boolean onCollideWithForceField(Level level, BlockPos pos, Entity entity, ItemStack stack) {
        return false;
    }

    @Override
    public void onCalculate(Projector projector, Collection<BlockPos> fieldDefinition) {}

    @Override
    public Set<Category> getCategories() {
        return this.categories;
    }
}
