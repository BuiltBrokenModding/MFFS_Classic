package com.builtbroken.mffs.common.items.modules.interdiction;

import com.builtbroken.mffs.api.security.IBiometricIdentifier;
import com.builtbroken.mffs.api.security.IInterdictionMatrix;
import com.builtbroken.mffs.api.security.Permission;
import com.builtbroken.mffs.common.items.modules.MatrixModule;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemModuleWarn extends MatrixModule
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "JFJ",
                'J', Blocks.jukebox,
                'F', Item.itemRegistry.getObject("mffs:focusMatrix")));
    }

    @Override
    public boolean onDefend(IInterdictionMatrix matri, Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            if (entity instanceof EntityPlayer)
            {
                EntityPlayer user = (EntityPlayer) entity;
                IBiometricIdentifier bio = matri.getBiometricIdentifier();
                if (bio != null &&
                        bio.isAccessGranted(user.getGameProfile().getName(), Permission.BYPASS_DEFENSE))
                {
                    return false;
                }
                user.addChatMessage(new ChatComponentText("[" + matri.getInventoryName() + "] " + LanguageRegistry.instance().getStringLocalization("message.moduleWarn.warn")));
            }
        }
        return false;
    }
}
