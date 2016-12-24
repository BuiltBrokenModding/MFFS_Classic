package com.mffs.common.items.card;

import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.mffs.RegisterManager;
import com.mffs.api.card.ICoordLink;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Calclavia
 */
public class ItemCardLink extends ItemCardBlank implements ICoordLink {

    /* This is the local version for caching */
    private Location link;

    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        NBTTagCompound tag = RegisterManager.getTag(stack);
        Location link = getLink(stack);
        if (link != null) {
            World world = link.getWorld();
            Block block = link.getBlock(world);
            if (block != null) {
                list.add(LanguageRegistry.instance().getStringLocalization("info.item.linkedWith") + " " + block.getLocalizedName());
            }
            list.add(link.xi() + ", " + link.yi() + ", " + link.zi());
            list.add(LanguageRegistry.instance().getStringLocalization("info.item.dimension") + " " + world.getWorldInfo().getWorldName());
        } else {
            super.addInformation(stack, usr, list, dummy);
            list.add(EnumChatFormatting.RED + LanguageRegistry.instance().getStringLocalization("info.item.notLinked"));
        }
    }

    @Override
    public void setLink(ItemStack paramItemStack, Location paramVectorWorld) {
        NBTTagCompound tag = RegisterManager.getTag(paramItemStack);
        tag.setTag("mffs_link", paramVectorWorld.toNBT());
        this.link = paramVectorWorld;
    }

    @Override
    public Location getLink(ItemStack paramItemStack) {
        NBTTagCompound tag = RegisterManager.getTag(paramItemStack);
        if (!tag.hasKey("mffs_link")) {
            return null;
        }
        NBTTagCompound linkTag = tag.getCompoundTag("mffs_link");
        if (link == null) {
            if(tag.hasKey("id"))
            {
                tag.setInteger("dimension", tag.getInteger("id"));
                tag.removeTag("id");
            }
            return link = new Location(linkTag);
        }
        //Since we cache it, we really do not need to obtain it every single time.
        //We just update our variable!
        //link.xCoord = linkTag.getInteger("x");
        //link.yCoord = linkTag.getInteger("y");
        //link.zCoord = linkTag.getInteger("z");
        //link.dimensionId = linkTag.getInteger("id");
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
            Location coord = new Location(world, x, y, z);
            setLink(stack, coord);

            Block block = coord.getBlock(world);
            if (block != null) {
                player.addChatMessage(new ChatComponentText(String.format(LanguageRegistry.instance().getStringLocalization("info.item.linkedWith") + " %d %d %d %s", x, y, z, block.getLocalizedName())));
            }
        }
        return true;
    }

    @Override
    public void genRecipes(List<IRecipe> list) {
        list.add(newShapedRecipe(this, "CWC", 'W', UniversalRecipe.WIRE.get(), 'C', Item.itemRegistry.getObject("mffs:cardBlank")));
    }
}
