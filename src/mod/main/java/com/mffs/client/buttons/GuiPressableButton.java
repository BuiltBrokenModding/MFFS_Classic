package com.mffs.client.buttons;

import com.mffs.MFFS;
import com.mffs.client.gui.MFFSGui;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
public class GuiPressableButton extends GuiButton {

    public static final ResourceLocation GUI_BUTTON = new ResourceLocation(MFFS.MODID, "textures/gui/gui_button.png");

    public boolean stuck = false;
    protected Vector2d offset = new Vector2d();
    private MFFSGui mainGui;

    public GuiPressableButton(int id, int x, int y, Vector2d offset, MFFSGui mainGui, String name) {
        super(id, x, y, 18, 18, name);
        this.offset = offset;
        this.mainGui = mainGui;
    }

    public GuiPressableButton(int id, int x, int y, Vector2d offset, MFFSGui mainGui) {
        this(id, x, y, offset, mainGui, "");
    }

    public GuiPressableButton(int id, int x, int y, Vector2d offset) {
        this(id, x, y, offset, null, "");
    }

    public GuiPressableButton(int id, int x, int y) {
        this(id, x, y, new Vector2d());
    }


    public void drawButton(Minecraft minecraft, int x, int y) {
        if (this.visible) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(GUI_BUTTON);

            if (this.stuck) {
                GL11.glColor4f(0.6F, 0.6F, 0.6F, 1.0F);
            } else if (isPointInRegion(this.xPosition, this.yPosition, this.width, this.height, x, y)) {
                GL11.glColor4f(0.85F, 0.85F, 0.85F, 1.0F);
            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            drawTexturedModalRect(this.xPosition, this.yPosition, (int) this.offset.x, (int) this.offset.y, this.width, this.height);
            func_73739_b(minecraft, x, y);
        }
    }


    protected void func_73739_b(Minecraft minecraft, int x, int y) {
        if (this.mainGui != null && this.displayString != null && this.displayString.length() > 0) {
            if (isPointInRegion(this.xPosition, this.yPosition, this.width, this.height, x, y)) {
                String title = LanguageRegistry.instance().getStringLocalization("gui." + this.displayString + ".name");

                this.mainGui.tooltip = LanguageRegistry.instance().getStringLocalization("gui." + this.displayString + ".tooltip");
                if ((title != null) && (title.length() > 0)) {
                    this.mainGui.tooltip = (title + ": " + this.mainGui.tooltip);
                }
            }
        }
    }

    protected boolean isPointInRegion(int x, int y, int width, int height, int checkX, int checkY) {
        int var7 = 0;
        int var8 = 0;
        checkX -= var7;
        checkY -= var8;
        return (checkX >= x - 1) && (checkX < x + width + 1) && (checkY >= y - 1) && (checkY < y + height + 1);
    }
}
