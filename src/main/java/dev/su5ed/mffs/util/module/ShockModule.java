package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShockModule extends BaseModule {
    public ShockModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onCollideWithForceField(World world, BlockPos pos, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            ModUtil.shockEntity(entity, this.stack.getCount());
        }
        return super.onCollideWithForceField(world, pos, entity);
    }
}
