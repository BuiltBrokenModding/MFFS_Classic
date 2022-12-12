package dev.su5ed.mffs.item;

import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShockModuleItem extends ModuleItem {

    public ShockModuleItem() {
        super(ModItems.itemProperties());
    }

    @Override
    public boolean onCollideWithForceField(Level level, BlockPos pos, Entity entity, ItemStack moduleStack) {
        if (entity instanceof LivingEntity living) {
            living.hurt(ModObjects.FIELD_SHOCK, moduleStack.getCount());
        }
        return super.onCollideWithForceField(level, pos, entity, moduleStack);
    }
}
