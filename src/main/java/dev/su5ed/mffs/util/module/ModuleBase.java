package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Set;

public class ModuleBase implements Module {
    private final float fortronCost;
    private final Set<Category> categories;

    public ModuleBase(Category... categories) {
        this(0.5F, categories);
    }

    public ModuleBase(float fortronCost) {
        this(fortronCost, Category.MATRIX);
    }

    public ModuleBase(float fortronCost, Category... categories) {
        this.fortronCost = fortronCost;
        this.categories = Set.of(categories);
    }

    @Override
    public float getFortronCost(float amplifier) {
        return this.fortronCost * amplifier;
    }

    @Override
    public boolean beforeProject(Projector projector, Collection<? extends BlockPos> field) {
        return false;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
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
