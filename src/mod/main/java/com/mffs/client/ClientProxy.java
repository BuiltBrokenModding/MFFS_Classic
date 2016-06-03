package com.mffs.client;

import com.mffs.CommonProxy;
import com.mffs.client.gui.GuiCoercionDeriver;
import com.mffs.model.container.CoercionDeriverContainer;
import com.mffs.model.tile.type.EntityCoercionDeriver;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by pwaln on 5/22/2016.
 */
public class ClientProxy extends CommonProxy {
    /**
     * Called before the main INITIALIZE.
     * @param event Forge ModLoader event.
     */
    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    /**
     * Called along with the main Initialize.
     * @param event Forge ModLoader event.
     */
    @Override
    public void init(FMLInitializationEvent event)
    {
    }

    /**
     * Called after the main Init.
     * @param event Forge ModLoader event.
     */
    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    /**
     * Returns a Container to be displayed to the user. On the client side, this
     * needs to return a instance of GuiScreen On the server side, this needs to
     * return a instance of Container
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity != null) {
            if(tileEntity instanceof EntityCoercionDeriver) {
                return new GuiCoercionDeriver(player, (EntityCoercionDeriver)tileEntity);
            }
        }
        return null;
    }
}
