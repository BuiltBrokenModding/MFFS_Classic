package com.mffs.client.gui.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
@SideOnly(Side.CLIENT)
public class GuiScroll
{

    /* Bar expansion */
    private float bar_expansion;

    /* The height of the scroll */
    private int height;

    /**
     * @param height
     */
    public GuiScroll(int height)
    {
        this.height = height;
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0)
        {
            scroll = Math.min(Math.max(scroll, -1), 1);
            bar_expansion = bar_expansion - (float) scroll / height;
        }
    }

    public float getBar()
    {
        return Math.min(Math.max(bar_expansion, 0), 1);
    }
}
