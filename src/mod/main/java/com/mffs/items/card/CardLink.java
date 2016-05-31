package com.mffs.items.card;

import com.mffs.RegisterManager;
import com.mffs.api.IItemFrequency;
import com.mffs.api.card.ICoordLink;
import com.mffs.api.utils.Util;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.common.registry.LanguageRegistry;
import mekanism.api.Coord4D;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

/**
 * Created by pwaln on 5/31/2016.
 */
public class CardLink extends CardBlank implements ICoordLink {

    /* This is the local version for caching */
    private Coord4D link;

    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List list, boolean dummy) {
        NBTTagCompound tag = RegisterManager.getTag(stack);
        Coord4D link = getLink(stack);
        if(link != null) {
            World world = DimensionManager.getWorld(link.dimensionId);
            Block block = link.getBlock(world);
            if(block != null) {
                list.add(LanguageRegistry.instance().getStringLocalization("info.item.linkedWith") + " " + block.getLocalizedName());
            }
            list.add(link.xCoord + ", " + link.yCoord + ", " + link.zCoord);
            list.add(LanguageRegistry.instance().getStringLocalization("info.item.dimension") + " " + world.getWorldInfo().getWorldName());
        } else {
            super.addInformation(stack, usr, list, dummy);
            list.add(EnumChatFormatting.RED+LanguageRegistry.instance().getStringLocalization("info.item.notLinked"));
        }
    }

    @Override
    public void setLink(ItemStack paramItemStack, Coord4D paramVectorWorld) {
        NBTTagCompound tag = RegisterManager.getTag(paramItemStack);
        tag.setTag("mffs_link", paramVectorWorld.write(tag.getCompoundTag("mffs_link")));
    }

    @Override
    public Coord4D getLink(ItemStack paramItemStack) {
        NBTTagCompound tag = RegisterManager.getTag(paramItemStack);
        if(!tag.hasKey("mffs_link")) {
            return null;
        }
        NBTTagCompound linkTag = tag.getCompoundTag("mffs_link");
        if(link == null) {
            return link = Coord4D.read(linkTag);
        }
        //We just update our variable!
        link.xCoord = linkTag.getInteger("x");
        link.yCoord = linkTag.getInteger("y");
        link.zCoord = linkTag.getInteger("z");
        link.dimensionId = linkTag.getInteger("id");
        return link;
    }
}
