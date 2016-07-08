package com.mffs.api.gui;

import com.mffs.MFFS;
import com.mffs.api.utils.CompatibilityType;
import com.mffs.api.utils.UnitDisplay;
import com.mffs.api.utils.Util;
import com.mffs.api.vector.Matrix2d;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Calclavia
 */
public class GuiContainerBase extends GuiContainer {

    public static final ResourceLocation GUI_COMPONENTS = new ResourceLocation(MFFS.MODID, "textures/gui/gui_components.png");

    public static final ResourceLocation baseTexture = new ResourceLocation(MFFS.MODID, "textures/gui/gui_base.png");
    ;
    public String tooltip = "";
    protected int meterX = 54;
    protected int meterHeight = 49;
    protected int meterWidth = 14;
    protected int meterEnd = this.meterX + this.meterWidth;
    protected int energyType = 0;
    protected HashMap<Matrix2d, String> tooltips = new HashMap();
    protected int containerWidth;
    protected int containerHeight;
    private float lastChangeFrameTime;

    public GuiContainerBase(Container container) {
        super(container);
        this.ySize = 217;
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        Iterator<Map.Entry<Matrix2d, String>> it = this.tooltips.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Matrix2d, String> entry = (Map.Entry) it.next();
            if (entry.getKey().isIn(mouseX - this.guiLeft, mouseY - this.guiTop)) {
                this.tooltip = (entry.getValue());
                break;
            }
        }
        if ((this.tooltip != null) && (this.tooltip != "")) {
            drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, (String[]) Util.splitStringPerWord(this.tooltip, 5).toArray(new String[0]));
        }
        this.tooltip = "";
    }

    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        this.containerWidth = ((this.width - this.xSize) / 2);
        this.containerHeight = ((this.height - this.ySize) / 2);

        this.mc.renderEngine.bindTexture(this.baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize, this.ySize);
    }

    protected void drawBulb(int x, int y, boolean isOn) {
        this.mc.renderEngine.bindTexture(this.baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (isOn) {
            drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 0, 6, 6);
        } else {
            drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 4, 6, 6);
        }
    }

    protected void drawSlot(int x, int y, ItemStack itemStack) {
        this.mc.renderEngine.bindTexture(this.baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

        drawItemStack(itemStack, this.containerWidth + x, this.containerHeight + y);
    }

    protected void drawItemStack(ItemStack itemStack, int x, int y) {
        x++;
        y++;
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);

        itemRender.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemStack, x, y);
    }

    protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY) {
        drawTextWithTooltip(textName, format, x, y, mouseX, mouseY, 4210752);
    }

    protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY, int color) {
        String name = LanguageRegistry.instance().getStringLocalization("gui." + textName + ".name");
        String text = format.replaceAll("%1", name);
        this.mc.fontRenderer.drawString(text, x, y, color);

        String tooltip = LanguageRegistry.instance().getStringLocalization("gui." + textName + ".tooltip");
        if ((tooltip != null) && (tooltip != "")) {
            if (func_146978_c(x, y, (int) (text.length() * 4.8), 12, mouseX, mouseY)) {
                this.tooltip = tooltip;
            }
        }
    }

    protected void drawTextWithTooltip(String textName, int x, int y, int mouseX, int mouseY) {
        drawTextWithTooltip(textName, "%1", x, y, mouseX, mouseY);
    }

    protected void drawSlot(int x, int y, GuiSlotType type, float r, float g, float b) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(r, g, b, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);
        if (type != GuiSlotType.NONE) {
            drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18 * type.ordinal(), 18, 18);
        }
    }

    protected void drawSlot(int x, int y, GuiSlotType type) {
        drawSlot(x, y, type, 1.0F, 1.0F, 1.0F);
    }

    protected void drawSlot(int x, int y) {
        drawSlot(x, y, GuiSlotType.NONE);
    }

    protected void drawBar(int x, int y, float scale) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 0, 22, 15);
        if (scale > 0.0F) {
            drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 15, 22 - (int) (scale * 22.0F), 15);
        }
    }

    protected void drawForce(int x, int y, float scale) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);
        if (scale > 0.0F) {
            drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, this.meterX, 11, (int) (scale * 107.0F), 11);
        }
    }

    protected void drawElectricity(int x, int y, float scale) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);
        if (scale > 0.0F) {
            drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 22, (int) (scale * 107.0F), 11);
        }
    }

    protected void drawMeter(int x, int y, float scale, float r, float g, float b) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, this.meterWidth, this.meterHeight);

        GL11.glColor4f(r, g, b, 1.0F);
        int actualScale = (int) ((this.meterHeight - 1) * scale);
        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y + (this.meterHeight - 1 - actualScale), 40, 49, this.meterHeight - 1, actualScale);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 98, this.meterWidth, this.meterHeight);
    }

    protected void drawMeter(int x, int y, float scale, FluidStack liquidStack) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, this.meterWidth, this.meterHeight);
        if (liquidStack != null) {
            displayGauge(this.containerWidth + x, this.containerHeight + y, -10, 1, 12, (int) ((this.meterHeight - 1) * scale), liquidStack);
        }
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 98, this.meterWidth, this.meterHeight);
    }

    public void renderUniversalDisplay(int x, int y, float energy, int mouseX, int mouseY, UnitDisplay.Unit unit) {
        String displaySuffix = "";
        if (unit == UnitDisplay.Unit.WATT) {
            displaySuffix = "/s";
        }
        String display = UnitDisplay.getDisplay(energy, unit);
        if ((unit == UnitDisplay.Unit.WATT) || (unit == UnitDisplay.Unit.JOULES)) {
            switch (this.energyType) {
                case 1:
                    display = UnitDisplay.roundDecimals(energy * CompatibilityType.BUILDCRAFT.ratio) + " MJ" + displaySuffix;
                    break;
                case 2:
                    display = UnitDisplay.roundDecimals(energy * CompatibilityType.INDUSTRIALCRAFT.ratio) + " EU" + displaySuffix;
                    break;
                case 3:
                    display = UnitDisplay.roundDecimals(energy * CompatibilityType.THERMAL_EXPANSION.ratio) + " RF" + displaySuffix;
            }
        }
        if (func_146978_c(x, y, display.length() * 5, 9, mouseX, mouseY)) {
            if ((Mouse.isButtonDown(0)) && (this.lastChangeFrameTime <= 0.0F)) {
                this.energyType = ((this.energyType + 1) % 4);
                this.lastChangeFrameTime = 30.0F;
            } else {
                drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Click to change unit.");
            }
        }
        this.lastChangeFrameTime -= 1.0F;

        this.mc.fontRenderer.drawString(display, x, y, 4210752);
    }

    public void drawTooltip(int x, int y, String... toolTips) {
        if (!GuiScreen.isShiftKeyDown()) {
            if (toolTips != null) {
                GL11.glDisable(32826);
                GL11.glDisable(2929);

                int var5 = 0;
                for (int var6 = 0; var6 < toolTips.length; var6++) {
                    int var7 = this.mc.fontRenderer.getStringWidth(toolTips[var6]);
                    if (var7 > var5) {
                        var5 = var7;
                    }
                }
                int var6 = x + 12;
                int var7 = y - 12;

                int var9 = 8;
                if (toolTips.length > 1) {
                    var9 += 2 + (toolTips.length - 1) * 10;
                }
                if (this.guiTop + var7 + var9 + 6 > this.height) {
                    var7 = this.height - var9 - this.guiTop - 6;
                }
                this.zLevel = 300.0F;
                int var10 = -267386864;
                drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
                drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
                drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
                drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
                drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
                int var11 = 1347420415;
                int var12 = (var11 & 0xFEFEFE) >> 1 | var11 & 0xFF000000;
                drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
                drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
                drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
                drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);
                for (int var13 = 0; var13 < toolTips.length; var13++) {
                    String var14 = toolTips[var13];

                    this.mc.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
                    var7 += 10;
                }
                this.zLevel = 0.0F;

                GL11.glEnable(2929);
                GL11.glEnable(32826);
            }
        }
    }

    protected void displayGauge(int j, int k, int line, int col, int width, int squaled, FluidStack liquid) {

        if (liquid == null) {
            return;
        }
        int start = 0;

        IIcon liquidIcon = null;
        Fluid fluid = liquid.getFluid();
        if ((fluid != null) && (fluid.getStillIcon() != null)) {
            liquidIcon = fluid.getStillIcon();
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(FMLClientHandler.instance().getClient().renderEngine.getResourceLocation(fluid.getSpriteNumber()));
        if (liquidIcon != null) {
            for (; ; ) {
                int x;
                if (squaled > 16) {
                    x = 16;
                    squaled -= 16;
                } else {
                    x = squaled;
                    squaled = 0;
                }
                drawTexturedModelRectFromIcon(j + col, k + line + 58 - x - start, liquidIcon, width, 16 - (16 - x));
                start += 16;
                if ((x == 0) || (squaled == 0)) {
                    break;
                }
            }
        }
    }
}
