package com.mffs.common.items.modules.interdiction;

import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.common.items.modules.MatrixModule;
import com.mffs.common.items.modules.projector.ItemModuleShock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;

/**
 * @author Calclavia
 */
public class ItemModuleAntiHostile extends MatrixModule {

    @Override
    public boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, EntityLivingBase paramEntityLivingBase) {
        if (paramEntityLivingBase instanceof IMob) {
            paramEntityLivingBase.attackEntityFrom(ItemModuleShock.SHOCK_SOURCE, 20F);
        }
        return false;
    }

}
