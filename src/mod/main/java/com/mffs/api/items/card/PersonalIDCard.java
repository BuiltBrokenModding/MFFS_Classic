package com.mffs.api.items.card;

import com.mffs.MFFS;
import com.mffs.api.ItemManager;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * Created by pwaln on 5/29/2016.
 */
public class PersonalIDCard extends Item {

    public PersonalIDCard() {
        super();
        setHasSubtypes(true);
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
        tooltip.add(String.format("Owner: %s", ItemManager.getTag(stack).getString("name")));
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rights"));
        } else {
            tooltip.add(LanguageRegistry.instance().getStringLocalization("itemInfo.rightsHoldShift"));
        }
    }
}
