package com.mffs.common.items.card;

import com.mffs.RegisterManager;
import com.mffs.api.card.ICoordLink;
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
public class CardLink extends CardBlank implements ICoordLink {

    /* This is the local version for caching */
    private Coord4D link;

    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        NBTTagCompound tag = RegisterManager.getTag(stack);
        Coord4D link = getLink(stack);
        if (link != null) {
            World world = DimensionManager.getWorld(link.dimensionId);
            Block block = link.getBlock(world);
            if (block != null) {
                list.add(LanguageRegistry.instance().getStringLocalization("info.item.linkedWith") + " " + block.getLocalizedName());
            }
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
        tag.setTag("mffs_link", paramVectorWorld.write(tag.getCompoundTag("mffs_link")));
        this.link = paramVectorWorld;
    }

    @Override
    public Coord4D getLink(ItemStack paramItemStack) {
        NBTTagCompound tag = RegisterManager.getTag(paramItemStack);
        if (!tag.hasKey("mffs_link")) {
            return null;
        }
        NBTTagCompound linkTag = tag.getCompoundTag("mffs_link");
        if (link == null) {
            return link = Coord4D.read(linkTag);
        }
        //TODO: Since we cache it, we really do not need to obtain it every single time.
        //We just update our variable!
        link.xCoord = linkTag.getInteger("x");
        link.yCoord = linkTag.getInteger("y");
        link.zCoord = linkTag.getInteger("z");
        link.dimensionId = linkTag.getInteger("id");
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
                player.addChatMessage(new ChatComponentText(String.format(LanguageRegistry.instance().getStringLocalization("info.item.linkedWith") + " %d %d %d %s", x, y, z, block.getLocalizedName())));
            }
        }
        return true;
    }
}
