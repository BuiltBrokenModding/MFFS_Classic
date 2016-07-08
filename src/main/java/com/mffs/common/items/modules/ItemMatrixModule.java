package com.mffs.common.items.modules;

import com.mffs.api.modules.IInterdictionMatrixModule;
import com.mffs.api.security.IInterdictionMatrix;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Calclavia
 */
public abstract class ItemMatrixModule extends ItemModule implements IInterdictionMatrixModule {

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param list
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        list.add("ยง4" + LanguageRegistry.instance().getStringLocalization("tile.mffs:interdictionMatrix.name"));
        super.addInformation(stack, usr, list, dummy);
    }

    @Override
    public boolean onDefend(IInterdictionMatrix paramIInterdictionMatrix, EntityLivingBase paramEntityLivingBase) {
        return false;
    }
}
