package com.mffs.common.items.modules.interdiction;

import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.common.items.modules.MatrixModule;
import com.mffs.common.items.modules.projector.ItemModuleShock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;

/**
 * @author Calclavia
 */
public class ItemModuleAntiFriendly extends MatrixModule {

    @Override
    public boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, EntityLivingBase paramEntityLivingBase) {
        if (paramEntityLivingBase instanceof INpc || !(paramEntityLivingBase instanceof IMob)) {
            paramEntityLivingBase.setHealth(1F);
            paramEntityLivingBase.attackEntityFrom(ItemModuleShock.SHOCK_SOURCE, 100F);
        }
        return false;
    }
}
