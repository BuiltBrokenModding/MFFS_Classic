package com.mffs.client;

import com.mffs.CommonProxy;
import com.mffs.ModularForcefieldSystem;
import com.mffs.api.vector.Vector3D;
import com.mffs.client.gui.GuiBiometricIdentifier;
import com.mffs.client.gui.GuiCoercionDeriver;
import com.mffs.client.gui.GuiForceFieldProjector;
import com.mffs.client.gui.GuiFortronCapacitor;
import com.mffs.client.gui.items.GuiCardID;
import com.mffs.client.render.*;
import com.mffs.client.render.particles.FortronBeam;
import com.mffs.client.render.particles.MovingFortron;
import com.mffs.common.tile.type.TileBiometricIdentifier;
import com.mffs.common.tile.type.TileCoercionDeriver;
import com.mffs.common.tile.type.TileForceFieldProjector;
import com.mffs.common.tile.type.TileFortronCapacitor;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Created by pwaln on 5/22/2016.
 */
public class ClientProxy extends CommonProxy {
    /**
     * Called before the main INITIALIZE.
     */
    @Override
    public void preInit() {
    }

    /**
     * Called along with the main Initialize.
     */
    @Override
    public void init() {
        RenderingRegistry.registerBlockHandler(new RenderBlockHandler());
        RenderingRegistry.registerBlockHandler(new RenderForceFieldHandler());
        MinecraftForgeClient.registerItemRenderer((Item) Item.itemRegistry.getObject(ModularForcefieldSystem.MODID + ":cardID"), new RenderIDCard());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCoercionDeriver.class, new RenderCoercionDeriver());
        ClientRegistry.bindTileEntitySpecialRenderer(TileForceFieldProjector.class, new RenderForceFieldProjector());
        ClientRegistry.bindTileEntitySpecialRenderer(TileFortronCapacitor.class, new RenderFortronCapacitor());
    }

    /**
     * Called after the main Init.
     */
    @Override
    public void postInit() {
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
        if(ID == 0) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                if (tileEntity instanceof TileCoercionDeriver) {
                    return new GuiCoercionDeriver(player, (TileCoercionDeriver) tileEntity);
                } else if (tileEntity instanceof TileForceFieldProjector) {
                    return new GuiForceFieldProjector(player, (TileForceFieldProjector) tileEntity);
                } else if (tileEntity instanceof TileFortronCapacitor) {
                    return new GuiFortronCapacitor(player, (TileFortronCapacitor) tileEntity);
                } else if (tileEntity instanceof TileBiometricIdentifier) {
                    return new GuiBiometricIdentifier(player, (TileBiometricIdentifier) tileEntity);
                }
            }
        } else if(ID == 1) {
            return new GuiCardID(player);
        }
        return null;
    }

    @Override
    public void registerBeamEffect(World world, Vector3D origin, Vector3D dest, float r, float g, float b, int lifespan) {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FortronBeam(world, origin, dest, r, g, b, lifespan));
    }

    @Override
    public void animateFortron(World world, Vector3D dest, float r, float g, float b, int life) {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new MovingFortron(world, dest, r, g, b, life));
    }
}
