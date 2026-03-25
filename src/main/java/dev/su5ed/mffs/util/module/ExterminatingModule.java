package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.util.InterdictionDamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ExterminatingModule extends BaseInterdictionModule {
    private final Predicate<EntityLivingBase> predicate;
    private final Supplier<Float> damagePerAction;

    public ExterminatingModule(ModuleType<?> type, ItemStack stack, Predicate<EntityLivingBase> predicate, Supplier<Float> damagePerAction) {
        super(type, stack);
        this.predicate = predicate;
        this.damagePerAction = damagePerAction;
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, EntityLivingBase target) {
        if (this.predicate.test(target)) {
            target.attackEntityFrom(new InterdictionDamageSource(interdictionMatrix), this.damagePerAction.get() * this.stack.getCount());
        }
        return false;
    }
}
