package com.mffs;

import com.mffs.model.fluids.Fortron;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by pwaln on 5/22/2016.
 */
public class InitCommon {
    /**
     * Called before the main INITIALIZE.
     * @param event Forge ModLoader event.
     */
    public void preInit(FMLPreInitializationEvent event) {
        Fortron.FLUID_ID = FluidRegistry.getFluidID("fortron");
    }

    /**
     * Called along with the main Initialize.
     * @param event Forge ModLoader event.
     */
    public void init(FMLInitializationEvent event){
    }

    /**
     * Called after the main Init.
     * @param event Forge ModLoader event.
     */
    public void postInit(FMLPostInitializationEvent event) {

    }
}
