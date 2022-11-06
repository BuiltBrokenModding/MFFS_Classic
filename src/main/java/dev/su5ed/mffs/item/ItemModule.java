package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.FieldInteraction;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class ItemModule extends Item implements Module {

    public ItemModule(Properties properties) {
        super(properties);
    }

    @Override
    public float getFortronCost(float amplifier) {
        return 0;
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
        return null;
    }

    @Override
    public void onCalculate(FieldInteraction projector, Set<Vec3> fieldDefinition) {

    }

    @Override
    public boolean requireTicks(ItemStack moduleStack) {
        return false;
    }
}
