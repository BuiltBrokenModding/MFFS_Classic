package com.mffs.common.items.modules.interdiction;

import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.common.items.modules.ItemMatrixModule;
import com.mffs.common.items.modules.projector.ModuleShock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;

/**
 * @author Calclavia
 */
public class ModuleAntiHostile extends ItemMatrixModule {

    @Override
    public boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, EntityLivingBase paramEntityLivingBase) {
        if (paramEntityLivingBase instanceof IMob) {
            paramEntityLivingBase.attackEntityFrom(ModuleShock.SHOCK_SOURCE, 20F);
        }
        return false;
    }

}
