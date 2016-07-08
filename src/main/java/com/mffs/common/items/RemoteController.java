package com.mffs.common.items;

import com.mffs.RegisterManager;
import com.mffs.api.card.ICoordLink;
import com.mffs.common.items.card.CardFrequency;
import cpw.mods.fml.common.registry.LanguageRegistry;
import mekanism.api.Coord4D;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

/**
 * @author Calclavia
 */
public class RemoteController extends CardFrequency implements ICoordLink {

    /* This is the local version for caching */
    private Coord4D link;

    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        super.addInformation(stack, usr, list, dummy);

        NBTTagCompound tag = RegisterManager.getTag(stack);
        Coord4D link = getLink(stack);
        if (link != null) {
            World world = DimensionManager.getWorld(link.dimensionId);
            Block block = link.getBlock(world);
            if (block != null)
                list.add(LanguageRegistry.instance().getStringLocalization("info.item.linkedWith") + " " + block.getLocalizedName());

            list.add(link.xCoord + ", " + link.yCoord + ", " + link.zCoord);
            list.add(LanguageRegistry.instance().getStringLocalization("info.item.dimension") + " " + world.getWorldInfo().getWorldName());
        } else {
            super.addInformation(stack, usr, list, dummy);
            list.add(EnumChatFormatting.RED + LanguageRegistry.instance().getStringLocalization("info.item.notLinked"));
        }
    }

    @Override
    public void setLink(ItemStack paramItemStack, Coord4D paramVectorWorld) {
        NBTTagCompound tag = RegisterManager.getTag(paramItemStack);
        this.link = paramVectorWorld;
        tag.setTag("mffs_link", paramVectorWorld.write(tag.getCompoundTag("mffs_link")));
    }


    @Override
    public Coord4D getLink(ItemStack paramItemStack) {
        NBTTagCompound tag = RegisterManager.getTag(paramItemStack);
        if (!tag.hasKey("mffs_link"))
            return null;
        if (link == null)
            return link = Coord4D.read(tag.getCompoundTag("mffs_link"));
        return link;
    }


    /**
     * This is called when the item is used, before the block is activated.
     *
     * @param stack  The Item Stack
     * @param player The Player that used the item
     * @param world  The Current World
     * @param x      Target X Position
     * @param y      Target Y Position
     * @param z      Target Z Position
     * @param side   The side of the target hit
     * @param hitX
     * @param hitY
     * @param hitZ   @return Return true to prevent any further processing.
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            Coord4D coord = new Coord4D(x, y, z, world.provider.dimensionId);
            setLink(stack, coord);

            Block block = coord.getBlock(world);
            if (block != null) {
                player.addChatMessage(new ChatComponentText(String.format(LanguageRegistry.instance().getStringLocalization("info.remoteController.linked")
                        .replace("%p", x + ", " + y + ", " + z)
                        .replace("%q", block.getLocalizedName()))));
            }
        }
        return true;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     *
     * @param stack
     * @param wrld
     * @param usr
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World wrld, EntityPlayer usr) {
     /*if (!entityPlayer.func_70093_af())
     {
       Vector3D position = getLink(itemStack);
       
       if (position != null)
       {
         int blockId = position.getBlockID(world);
         
         if (Block.field_71973_m[blockId] != null)
         {
           Chunk chunk = world.func_72938_d(position.intX(), position.intZ());
           
           if ((chunk != null) && (chunk.field_76636_d) && ((MFFSHelper.hasPermission(world, position, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, entityPlayer)) || (MFFSHelper.hasPermission(world, position, Permission.REMOTE_CONTROL, entityPlayer))))
           {
             float requiredEnergy = (float)Vector3D.distance(new Vector3D(entityPlayer), position) * 10.0F;
             int receivedEnergy = 0;
             
             Set<IFortronFrequency> fortronTiles = FrequencyGrid.instance().getFortronTiles(world, new Vector3D(entityPlayer), 50, getFrequency(itemStack));
             
             for (IFortronFrequency fortronTile : fortronTiles)
             {
               int consumedEnergy = fortronTile.requestFortron((int)Math.ceil(requiredEnergy / fortronTiles.size()), true);
               
               if (consumedEnergy > 0)
               {
                 if (world.field_72995_K)
                 {
                   ModularForceFieldSystem.proxy.renderBeam(world, new Vector3D(entityPlayer).add(new Vector3D(0.0D, entityPlayer.func_70047_e() - 0.2D, 0.0D)), new Vector3D((TileEntity)fortronTile).add(0.5D), 0.6F, 0.6F, 1.0F, 20);
                 }
                 
                 receivedEnergy += consumedEnergy;
               }
               
               if (receivedEnergy >= requiredEnergy)
               {
                 try
                 {
                   Block.field_71973_m[blockId].func_71903_a(world, position.intX(), position.intY(), position.intZ(), entityPlayer, 0, 0.0F, 0.0F, 0.0F);
                 }
                 catch (Exception e)
                 {
                   e.printStackTrace();
                 }
                 
                 return itemStack;
               }
             }
             
             if (!world.field_72995_K)
             {
               entityPlayer.func_71035_c(LanguageUtility.getLocal("message.remoteController.fail").replaceAll("%p", UnitDisplay.getDisplay(requiredEnergy, UnitDisplay.Unit.JOULES)));
             }
           }
         }
       }
     }*/
        return stack;
    }

    /*private final Set<ItemStack> temporaryRemoteBlacklist = new HashSet();
       
   @SubscribeEvent
   public void preMove(EventForceManipulate.EventPreForceManipulate evt)
   {
     this.temporaryRemoteBlacklist.clear();
   }
       
 
 
 
 
 
 
   @SubscribeEvent
   public void onMove(EventForceManipulate.EventPostForceManipulate evt)
   {
     if (!evt.world.field_72995_K)
     {
       for (ItemStack itemStack : this.remotesCached)
       {
         if ((!this.temporaryRemoteBlacklist.contains(itemStack)) && (new Vector3D(evt.beforeX, evt.beforeY, evt.beforeZ).equals(getLink(itemStack))))
         {
 
           setLink(itemStack, new VectorWorld(evt.world, evt.afterX, evt.afterY, evt.afterZ));
           this.temporaryRemoteBlacklist.add(itemStack);
         }
       }
     }
   }*/
}
