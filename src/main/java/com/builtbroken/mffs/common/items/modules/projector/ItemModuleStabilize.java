package com.builtbroken.mffs.common.items.modules.projector;

import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.event.EventStabilize;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.modules.BaseModule;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleSpeed;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Set;

/**
 * Created by pwaln on 6/17/2016.
 */
public class ItemModuleStabilize extends BaseModule implements IRecipeContainer
{

    @Override
    public void genRecipes(List<IRecipe> list)
    {
        list.add(newShapedRecipe(this,
                "FDF", "PSA", "FDF",
                'F', Item.itemRegistry.getObject("mffs:focusMatrix"),
                'D', Items.diamond,
                'P', Items.diamond_pickaxe,
                'S', Items.diamond_shovel,
                'A', Items.diamond_axe));
    }

    /* Keeps track of how many blocks duh */
    private short blockCount;

    /**
     * Default constructor.
     */
    public ItemModuleStabilize()
    {
        setCost(20);
        setMaxStackSize(1);
    }

    /**
     * Calls this on projection.
     *
     * @param projector The projector interface.
     * @param fields    A set of fields that are projected.
     * @return
     */
    @Override
    public boolean prePlaceFieldBlock(IProjector projector, Set<Vector3D> fields)
    {
        this.blockCount = 0;
        return false;
    }

    @Override
    public int prePlaceFieldBlock(IProjector projector, Vector3D position)
    {
        if (projector.getTicks() % 40 == 0)
        {
            //TODO: CUstom Mode
            //int[] blockInfo = null;
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                TileEntity proj = (TileEntity) projector;
                TileEntity entity = proj.getWorldObj().getTileEntity(proj.xCoord + dir.offsetX, proj.yCoord + dir.offsetY, proj.zCoord + dir.offsetZ);
                if (entity != null && entity instanceof IInventory)
                {
                    IInventory inv = (IInventory) entity;
                    for (int slot = 0; slot < inv.getSizeInventory(); slot++)
                    {
                        ItemStack stack = inv.getStackInSlot(slot);
                        if (stack != null)
                        {
                            EventStabilize event = new EventStabilize(proj.getWorldObj(), (int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z), stack);
                            MinecraftForge.EVENT_BUS.post(event);

                            if (!event.isCanceled())
                            {
                                if (stack.getItem() instanceof ItemBlock //TODO: Check block info if custom
                                        && entity.getWorldObj().canPlaceEntityOnSide(((ItemBlock) stack.getItem()).field_150939_a, (int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z),
                                        false, 0, null, stack))
                                {
                                    int meta = stack.getHasSubtypes() ? stack.getItemDamage() : 0/* Block info */;

                                    ((ItemBlock) stack.getItem()).placeBlockAt(stack, null, entity.getWorldObj(), (int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z), 0, 0, 0, 0, meta);
                                    inv.decrStackSize(slot, 1);
                                    //SEND Packet!
                                    //calclavia.lib.network.PacketHandler.sendPacketToClients(ModularForceFieldSystem.PACKET_TILE.getPacket((TileEntity)projector, new Object[] { Integer.valueOf(TileMFFS.TilePacketType.FXS.ordinal()), Integer.valueOf(1), Integer.valueOf(position.intX()), Integer.valueOf(position.intY()), Integer.valueOf(position.intZ()) }), ((TileEntity)projector).field_70331_k);
                                    if (blockCount++ >= projector.getModuleCount(ItemModuleSpeed.class) / 3)
                                    {
                                        return 2;
                                    }
                                    return 1;
                                }
                            }
                            else
                            {
                                return 1;
                            }
                        }
                    }
                }
            }
        }
        return 1;
    }

    @Override
    public int getFortronCost(float amplifier)
    {
        return Math.round(super.getFortronCost(amplifier) + super.getFortronCost(amplifier) * amplifier);
    }
}
