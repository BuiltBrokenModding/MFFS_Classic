package com.mffs.api.items.card;

import com.mffs.MFFS;
import com.mffs.api.RegisterManager;
import com.mffs.api.SecurityClearance;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Original MFFS File.
 * Credits: Thunderdark, Calclavia
 */
public class AccessCard extends PersonalIDCard {

    /* Amount of ticks on this item */
    private short tick;

    public AccessCard() {
        super();
        setHasSubtypes(true);
        setMaxStackSize(1);
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
        NBTTagCompound tag = RegisterManager.getTag(stack);
        tooltip.add(String.format(LanguageRegistry.instance().getStringLocalization("itemInfo.securityArea"), tag.getString("Areaname")));
        tooltip.add(String.format(LanguageRegistry.instance().getStringLocalization("itemInfo.periodOfValidity"), tag.getInteger("validity")));
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            NBTTagCompound rights = tag.getCompoundTag("rights");
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rights"));
            for(SecurityClearance clr : SecurityClearance.values()) {
                if(rights.getBoolean(clr.name())) {
                    tooltip.add("-"+clr.getName());
                }
            }
        } else {
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rightsHoldShift"));
        }
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     *
     * @param stack
     * @param p_77663_2_
     * @param p_77663_3_
     * @param p_77663_4_
     * @param p_77663_5_
     */
    @Override
    public void onUpdate(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        if(this.tick > 1_200) {
            NBTTagCompound tag = RegisterManager.getTag(stack);
            int timer = tag.getInteger("validate");
            if(timer > 0) {
                tag.setInteger("validate", timer - 1);
            }

            int SEC_ID = tag.getInteger("linkID");
            if(SEC_ID != 0) {

            }
            this.tick = 0;
        }
        this.tick++;
    }
}
