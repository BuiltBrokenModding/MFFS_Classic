package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class WarnModule extends BaseInterdictionModule {
    public WarnModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public boolean onDefend(InterdictionMatrix interdictionMatrix, EntityLivingBase target) {
        // The action zone warning is implicit — players feel the effects (confiscation, damage).
        // The proximity warning before the action zone is handled by the scanner as an action bar popup.
        return false;
    }
}
