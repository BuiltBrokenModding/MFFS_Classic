package com.mffs;

import com.mffs.model.fluids.Fortron;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Calclavia
 */
public class ModEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void preTextureHook(TextureStitchEvent event) {
        if(event.map.getTextureType() == 0)
            Fortron.fluidIcon = event.map.registerIcon(MFFS.MODID+":fortron");
    }


}
