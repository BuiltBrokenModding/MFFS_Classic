package com.mffs.client.gui.items;

import com.mffs.api.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
public abstract class GuiItemBase extends GuiContainerBase
{
    GuiTextField textField;

    public GuiItemBase(Container container)
    {
        super(container);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        textField = new GuiTextField(fontRendererObj, 50, 30, 80, 15);
    }

    @Override
    public void mouseClicked(int x, int y, int par3)
    {
        super.mouseClicked(x, y, par3);

        if (textField != null)
        {
            textField.mouseClicked(x - this.containerWidth, y - this.containerHeight, par3);
        }
    }

    @Override
    public void keyTyped(char c, int p_73869_2_)
    {
        if (p_73869_2_ == 1 || p_73869_2_ == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            super.keyTyped(c, p_73869_2_);
        }
        textField.textboxKeyTyped(c, p_73869_2_);
    }
}