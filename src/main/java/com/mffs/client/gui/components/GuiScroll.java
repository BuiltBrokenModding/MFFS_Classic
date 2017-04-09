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
    private float scrollPosScale;

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
            //Seems mouse scroll is between -120 to 120 - Dark
            //TODO fix as I think this go to 0 or 1 every time
            scroll = Math.min(Math.max(scroll, -1), 1);

            //Get new scroll position based on movement
            scrollPosScale = scrollPosScale - (float) scroll / height;

            //Limit scroll between 0 and 1
            scrollPosScale = Math.max(0, scrollPosScale);
            scrollPosScale = Math.min(1, scrollPosScale);
        }
    }

    public float getBar()
    {
        return scrollPosScale;
    }
}
