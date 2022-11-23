package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class ModuleItem extends Item implements Module {
    private final float fortronCost;

    public ModuleItem(Properties properties) {
        this(properties, 0.5F);
    }

    public ModuleItem(Properties properties, float fortronCost) {
        super(properties);

        this.fortronCost = fortronCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        
        tooltipComponents.add(Component.literal("Fortron: " + getFortronCost(1) * 20 + " L/s"));
    }

    @Override
    public float getFortronCost(float amplifier) {
        return this.fortronCost;
    }

    @Override
    public boolean onProject(Projector projector, Set<BlockPos> field) {
        return false;
    }

    @Override
    public boolean onDestroy(Projector projector, Set<BlockPos> field) {
        return false;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        return ProjectAction.PROJECT;
    }

    @Override
    public boolean onCollideWithForceField(Level level, int x, int y, int z, Entity entity, ItemStack moduleStack) {
        return false;
    }

    @Override
    public Set<Vec3> onPreCalculate(Projector projector, Set<Vec3> calculatedField) {
        return calculatedField;
    }

    @Override
    public void onCalculate(Projector projector, Set<BlockPos> fieldDefinition) {

    }

    @Override
    public boolean requireTicks(ItemStack moduleStack) {
        return false;
    }
}
