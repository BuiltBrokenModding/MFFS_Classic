package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleSupplier;

public class FortronChargeWidget extends GuiButton {
    public static final ResourceLocation COMPONENTS = new ResourceLocation(MFFSMod.MODID, "textures/gui/components.png");
    private static final AtomicInteger NEXT_ID = new AtomicInteger(500);

    private final int guiLeftRef;
    private final int guiTopRef;
    private final DoubleSupplier scale;

    /**
     * @param x      screen-space X (guiLeft + relX)
     * @param y      screen-space Y (guiTop + relY)
     * @param width  max bar width
     * @param height bar height (11)
     * @param scale  0.0–1.0 fill fraction supplier
     */
    public FortronChargeWidget(int x, int y, int width, int height, DoubleSupplier scale) {
        super(NEXT_ID.getAndIncrement(), x, y, width, height, "");
        this.guiLeftRef = 0;
        this.guiTopRef  = 0;
        this.scale = scale;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return false; // not clickable
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;
        double s = this.scale.getAsDouble();
        if (s <= 0) return;
        int drawWidth = (int) (s * this.width);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(COMPONENTS);
        // Draw a sub-region of the components texture (u=54, v=11)
        int u = 54, v = 11;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        double x1 = this.x, y1 = this.y;
        double x2 = x1 + drawWidth, y2 = y1 + this.height;
        buf.pos(x1, y2, 0).tex(u / 256.0, (v + this.height) / 256.0).endVertex();
        buf.pos(x2, y2, 0).tex((u + drawWidth) / 256.0, (v + this.height) / 256.0).endVertex();
        buf.pos(x2, y1, 0).tex((u + drawWidth) / 256.0, v / 256.0).endVertex();
        buf.pos(x1, y1, 0).tex(u / 256.0, v / 256.0).endVertex();
        tess.draw();
    }
}
