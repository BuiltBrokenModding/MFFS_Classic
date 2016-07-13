package com.mffs.common.items;

import com.mffs.RegisterManager;
import com.mffs.api.IBlockFrequency;
import com.mffs.api.card.ICoordLink;
import com.mffs.api.event.EventForceManipulate;
import com.mffs.api.event.EventPostForceManipulate;
import com.mffs.api.event.EventPreForceManipulate;
import com.mffs.api.fortron.FrequencyGrid;
import com.mffs.api.fortron.IFortronFrequency;
import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.api.security.Permission;
import com.mffs.api.utils.MatrixHelper;
import com.mffs.api.utils.UnitDisplay;
import com.mffs.api.vector.Vector3D;
import com.mffs.client.render.particles.FortronBeam;
import com.mffs.common.items.card.CardFrequency;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import mekanism.api.Coord4D;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public class RemoteController extends CardFrequency implements ICoordLink {

    /* This is the local version for caching */
    private Coord4D link;

    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        super.addInformation(stack, usr, list, dummy);

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
                if(!world.isRemote)
                    player.addChatMessage(new ChatComponentText(String.format(LanguageRegistry.instance().getStringLocalization("message.remoteController.linked")
                            .replace("%p", x + ", " + y + ", " + z).replace("%q", block.getLocalizedName()))));
            }
        }
        return true;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     *
     * @param stack
     * @param world
     * @param usr
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer usr) {
        if (!usr.isSneaking()) {
            Coord4D position = getLink(stack);

            if (position != null) {
                Block block = position.getBlock(world);

                if (block != null) {
                    Chunk chunk = world.getChunkFromBlockCoords(position.xCoord, position.zCoord);
                    Set<IBlockFrequency> freq = FrequencyGrid.instance().get();
                    Vector3D usrLoc = new Vector3D(usr);
                    IInterdictionMatrix matrix = MatrixHelper.findMatrix(world, usrLoc, freq);
                    if (chunk != null && chunk.isChunkLoaded && (matrix == null || MatrixHelper.checkActionPermission(matrix, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, usr)|| MatrixHelper.checkPermission(matrix, usr.getGameProfile().getName(), Permission.REMOTE_CONTROL))) {
                        float requiredEnergy = (float) usrLoc.distance(position.xCoord, position.yCoord, position.zCoord) * 10.0F;
                        int receivedEnergy = 0;

                        int freq_ = getFrequency(stack);
                        for(Iterator<IBlockFrequency> it$ = freq.iterator(); it$.hasNext();) {//By doing this we cut out a extra loop
                            IBlockFrequency b_freq = it$.next();
                            if(!(b_freq instanceof IFortronFrequency)
                                || usrLoc.distance(((TileEntity)b_freq).xCoord, ((TileEntity)b_freq).yCoord, ((TileEntity)b_freq).zCoord) > 50)
                                it$.remove();
                            else if(b_freq.getFrequency() != freq_)
                                it$.remove();
                        }

                        Vector3D center = usrLoc.clone().add(new Vector3D(0.0D, usr.getEyeHeight() - 0.2D, 0.0D));
                        for (IBlockFrequency bl : freq) {
                            IFortronFrequency fortronTile = (IFortronFrequency) bl;
                            int consumedEnergy = fortronTile.requestFortron((int) Math.ceil(requiredEnergy / freq.size()), true);

                            if (consumedEnergy > 0) {
                                if (world.isRemote)
                                    FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FortronBeam(world, center, new Vector3D((TileEntity)fortronTile).add(0.5), 0.6F, 0.6F, 1.0F, 20));
                                receivedEnergy += consumedEnergy;
                            }

                            if (receivedEnergy >= requiredEnergy) {
                                try {
                                    block.onBlockActivated(world, position.xCoord, position.yCoord, position.zCoord, usr, 0, 0.0F, 0.0F, 0.0F);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return stack;
                            }
                        }

                        if (!world.isRemote)
                            usr.addChatMessage(new ChatComponentText(LanguageRegistry.instance().getStringLocalization("message.remoteController.fail").replaceAll("%p", UnitDisplay.getDisplay(requiredEnergy, UnitDisplay.Unit.JOULES))));
                    }
                }
            }
        }
        return stack;
    }

    /*private final Set<ItemStack> temporaryRemoteBlacklist = new HashSet();
       
   @SubscribeEvent
   public void preMove(EventPreForceManipulate evt)
   {
     this.temporaryRemoteBlacklist.clear();
   }
       
 
 
 
 
 
 
   @SubscribeEvent
   public void onMove(EventPostForceManipulate evt)
   {
     if (!evt.world.isRemote)
     {
         if ((!this.temporaryRemoteBlacklist.contains(evt.)) && (new Vector3D(evt.beforeX, evt.beforeY, evt.beforeZ).equals(getLink(itemStack))))
         {
 
           setLink(itemStack, new VectorWorld(evt.world, evt.afterX, evt.afterY, evt.afterZ));
           this.temporaryRemoteBlacklist.add(itemStack);
       }
     }
   }*/
}
