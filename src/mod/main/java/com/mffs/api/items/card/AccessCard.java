package com.mffs.api.items.card;

import com.mffs.MFFS;
import com.mffs.api.ItemManager;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Created by pwaln on 5/29/2016.
 */
public class AccessCard extends Item {

    public AccessCard() {
        super();
        setHasSubtypes(true);
        setTextureName(MFFS.MODID+":AccessCard");
    }

    @Override
    public void registerIcons(IIconRegister p_94581_1_) {
        this.itemIcon = p_94581_1_.registerIcon(MFFS.MODID+":AccessCard");
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *
     * @param stack
     * @param usr
     * @param tooltip
     * @param dummy
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer usr, List tooltip, boolean dummy) {
        NBTTagCompound tag = ItemManager.getTag(stack);
        tooltip.add(String.format(LanguageRegistry.instance().getStringLocalization("itemInfo.securityArea"), tag.getString("Areaname")));
        tooltip.add(String.format(LanguageRegistry.instance().getStringLocalization("itemInfo.periodOfValidity"), tag.getInteger("validity")));
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            NBTTagCompound rights = tag.getCompoundTag("rights");
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rights"));
        } else {
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rightsHoldShift"));
        }
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     *
     * @param p_77663_1_
     * @param p_77663_2_
     * @param p_77663_3_
     * @param p_77663_4_
     * @param p_77663_5_
     */
    @Override
    public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {

    }
}
