package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShockModule extends BaseModule {

    public ShockModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onCollideWithForceField(Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.hurt(ModObjects.FIELD_SHOCK, this.stack.getCount());
        }
        return super.onCollideWithForceField(level, pos, entity);
    }
}
