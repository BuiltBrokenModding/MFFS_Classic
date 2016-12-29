package com.mffs.common.items.modules.interdiction;

import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.common.items.modules.MatrixModule;
import com.mffs.common.items.modules.projector.ItemModuleShock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleAntiFriendly extends MatrixModule {

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this,
                " W ", "LFP", " S ", 'W', Blocks.hay_block, //couldnt find wool block
                'L', Items.leather, 'F', Item.itemRegistry.getObject("mffs:focusMatrix"), 'P', Items.cooked_porkchop,
                'S', Items.slime_ball));
    }

    @Override
    public boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, EntityLivingBase paramEntityLivingBase) {
        if (paramEntityLivingBase instanceof INpc || !(paramEntityLivingBase instanceof IMob)) {
            paramEntityLivingBase.setHealth(1F);
            paramEntityLivingBase.attackEntityFrom(ItemModuleShock.SHOCK_SOURCE, 100F);
        }
        return false;
    }
}
