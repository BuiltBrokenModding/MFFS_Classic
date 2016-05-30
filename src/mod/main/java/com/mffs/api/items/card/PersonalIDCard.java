package com.mffs.api.items.card;

import com.mffs.MFFS;
import com.mffs.api.RegisterManager;
import com.mffs.api.SecurityClearance;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Original MFFS File.
 * Credits: Thunderdark, Calclavia
 */
public class PersonalIDCard extends Item {

    public PersonalIDCard() {
        super();
        setHasSubtypes(true);
        setMaxStackSize(1);
        setTextureName(MFFS.MODID+":PersonalIDCard");
    }

    @Override
    public void registerIcons(IIconRegister p_94581_1_) {
        this.itemIcon = p_94581_1_.registerIcon(MFFS.MODID+":PersonalIDCard");
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
        tooltip.add(String.format("Owner: %s", RegisterManager.getTag(stack).getString("name")));
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rights"));
            NBTTagCompound rights = RegisterManager.getTag(stack).getCompoundTag("rights");
            for(SecurityClearance clr : SecurityClearance.values()) {
                if(rights.getBoolean(clr.name())) {
                    tooltip.add("-"+clr.getName());
                }
            }
        } else {
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rightsHoldShift"));
        }
    }
}
