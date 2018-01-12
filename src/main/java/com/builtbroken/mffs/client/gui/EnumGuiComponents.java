package com.builtbroken.mffs.client.gui;

import com.builtbroken.mc.core.References;
import com.builtbroken.mffs.MFFS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

/**
 * Enum of entries contained in the GUI components texture sheet
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/12/2018.
 */
public enum EnumGuiComponents
{
    /** Default GUI slot */
    SLOT(0, 0),
    /** Redstone style dust */
    DUST(0, 1);

    public static ResourceLocation TEXTURE = new ResourceLocation(MFFS.DOMAIN, References.GUI_DIRECTORY + "gui_components.png");

    public static final int WIDTH = 18;
    public static final int HEIGHT = 18;

    public final int index_x;
    public final int index_y;

    EnumGuiComponents(int index_x, int index_y)
    {
        this.index_x = index_x;
        this.index_y = index_y;
    }

    public int getU()
    {
        return index_x * WIDTH;
    }

    public int getV()
    {
        return index_y * HEIGHT;
    }

    public void render(Gui gui, int x, int y)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        gui.drawTexturedModalRect(x, y, getU(), getV(), WIDTH, HEIGHT);
    }
}
