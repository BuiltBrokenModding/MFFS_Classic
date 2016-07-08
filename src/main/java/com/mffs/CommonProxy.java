package com.mffs;

import com.mffs.common.container.CoercionDeriverContainer;
import com.mffs.common.container.ForceFieldProjectorContainer;
import com.mffs.common.container.FortronCapacitorContainer;
import com.mffs.common.fluids.Fortron;
import com.mffs.common.tile.type.TileCoercionDeriver;
import com.mffs.common.tile.type.TileForceFieldProjector;
import com.mffs.common.tile.type.TileFortronCapacitor;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by pwaln on 5/22/2016.
 */
public class CommonProxy implements IGuiHandler {
    /**
     * Called before the main INITIALIZE.
     *
     * @param event Forge ModLoader event.
     */
    public void preInit(FMLPreInitializationEvent event) {
        Fortron.FLUID_ID = FluidRegistry.getFluidID("fortron");
    }

    /**
     * Called along with the main Initialize.
     *
     * @param event Forge ModLoader event.
     */
    public void init(FMLInitializationEvent event) {
    }

    /**
     * Called after the main Init.
     *
     * @param event Forge ModLoader event.
     */
    public void postInit(FMLPostInitializationEvent event) {

    }

    /**
     * Returns a Server side Container to be displayed to the user.
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
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            if (tileEntity instanceof TileCoercionDeriver) {
                return new CoercionDeriverContainer(player, (TileCoercionDeriver) tileEntity);
            } else if (tileEntity instanceof TileForceFieldProjector) {
                return new ForceFieldProjectorContainer(player, (TileForceFieldProjector) tileEntity);
            } else if (tileEntity instanceof TileFortronCapacitor) {
                return new FortronCapacitorContainer(player, (TileFortronCapacitor) tileEntity);
            }
        }
        return null;
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
        return null;
    }
}
