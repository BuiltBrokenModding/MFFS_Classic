package com.mffs.common.items.modules.interdiction;

import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.common.items.modules.MatrixModule;
import com.mffs.common.items.modules.projector.ItemModuleShock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleAntiHostile extends MatrixModule {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                " W ", "LFP", " S ",
                'W',   Items.rotten_flesh,
                'L', Items.gunpowder,
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'P', Items.bone,
                'S', Items.ghast_tear));
    }

    @Override
    public boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, EntityLivingBase paramEntityLivingBase) {
        if (paramEntityLivingBase instanceof IMob) {
            paramEntityLivingBase.attackEntityFrom(ItemModuleShock.SHOCK_SOURCE, 20F);
            return true;
        }
        return false;
    }

}
