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
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IconCycleButton<T extends Enum<T>> extends GuiButton {
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(MFFSMod.MODID, "textures/gui/buttons.png");
    private static final AtomicInteger NEXT_ID = new AtomicInteger(300);

    private final ResourceLocation image;
    private final int imageU;
    private final int imageV;
    private final int yStep;
    private final Supplier<T> value;
    private final Consumer<T> onPress;

    public IconCycleButton(int x, int y, int width, int height, int imageU, int imageV, int yStep, Supplier<T> value, Consumer<T> onPress) {
        this(x, y, width, height, GUI_BUTTONS, imageU, imageV, yStep, value, onPress);
    }

    public IconCycleButton(int x, int y, int width, int height, ResourceLocation image, int imageU, int imageV, int yStep, Supplier<T> value, Consumer<T> onPress) {
        super(NEXT_ID.getAndIncrement(), x, y, width, height, "");
        this.image = image;
        this.imageU = imageU;
        this.imageV = imageV;
        this.yStep = yStep;
        this.value = value;
        this.onPress = onPress;
    }

    /** Called by BaseScreen.actionPerformed() */
    public void firePress() {
        if (this.enabled) {
            this.onPress.accept(this.value.get());
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;
        this.hovered = mouseX >= this.x && mouseY >= this.y
            && mouseX < this.x + this.width && mouseY < this.y + this.height;
        float col = this.hovered ? 0.85F : 1.0F;
        GlStateManager.color(col, col, col, 1.0F);
        mc.getTextureManager().bindTexture(this.image);
        int vOffset = this.imageV + this.value.get().ordinal() * this.yStep;
        // Draw the full icon in one call
        blitRegion(this.x, this.y, this.imageU, vOffset, this.width, this.height);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void blitRegion(int x, int y, int u, int v, int w, int h) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(x,     y + h, 0).tex(u / 256.0,       (v + h) / 256.0).endVertex();
        buf.pos(x + w, y + h, 0).tex((u + w) / 256.0, (v + h) / 256.0).endVertex();
        buf.pos(x + w, y,     0).tex((u + w) / 256.0,  v / 256.0).endVertex();
        buf.pos(x,     y,     0).tex(u / 256.0,         v / 256.0).endVertex();
        tess.draw();
    }
}

