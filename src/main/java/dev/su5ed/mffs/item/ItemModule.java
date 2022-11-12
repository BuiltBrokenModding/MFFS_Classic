package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.FieldInteraction;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
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

public class ItemModule extends Item implements Module {
    private final float fortronCost;

    public ItemModule(Properties properties) {
        this(properties, 0.5F);
    }

    public ItemModule(Properties properties, float fortronCost) {
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
    public boolean onProject(Projector projector, Set<Vec3> field) {
        return false;
    }

    @Override
    public boolean onDestroy(Projector projector, Set<Vec3> field) {
        return false;
    }

    @Override
    public int onProject(Projector projector, Vec3 position) {
        return 0;
    }

    @Override
    public boolean onCollideWithForceField(Level level, int x, int y, int z, Entity entity, ItemStack moduleStack) {
        return false;
    }

    @Override
    public Set<Vec3> onPreCalculate(FieldInteraction projector, Set<Vec3> calculatedField) {
        return calculatedField;
    }

    @Override
    public void onCalculate(FieldInteraction projector, Set<Vec3> fieldDefinition) {

    }

    @Override
    public boolean requireTicks(ItemStack moduleStack) {
        return false;
    }
}
