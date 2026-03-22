package dev.su5ed.mffs.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class IconToggleButton extends GuiButton {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(400);

    private final ResourceLocation image;
    private final int imageU;
    private final int imageV;
    private final ITextComponent tooltip;
    private final BooleanSupplier value;
    private final Consumer<Boolean> onPress;

    public IconToggleButton(int x, int y, int width, int height, ITextComponent tooltip, int imageU, int imageV, BooleanSupplier value, Consumer<Boolean> onPress) {
        this(x, y, width, height, tooltip, IconCycleButton.GUI_BUTTONS, imageU, imageV, value, onPress);
    }

    public IconToggleButton(int x, int y, int width, int height, ITextComponent tooltip, ResourceLocation image, int imageU, int imageV, BooleanSupplier value, Consumer<Boolean> onPress) {
        super(NEXT_ID.getAndIncrement(), x, y, width, height, "");
        this.image = image;
        this.imageU = imageU;
        this.imageV = imageV;
        this.tooltip = tooltip;
        this.value = value;
        this.onPress = onPress;
    }

    public ITextComponent getTooltip() {
        return this.tooltip;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    /** Called by BaseScreen.actionPerformed() */
    public void firePress() {
        if (this.enabled) {
            this.onPress.accept(this.value.getAsBoolean());
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;
        this.hovered = mouseX >= this.x && mouseY >= this.y
            && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (this.value.getAsBoolean()) {
            GlStateManager.color(0.45F, 1.0F, 0.45F, 1.0F); // slight green tint when active
        } else if (this.hovered) {
            GlStateManager.color(0.85F, 0.85F, 0.85F, 1.0F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        mc.getTextureManager().bindTexture(this.image);
        blitRegion(this.x, this.y, this.imageU, this.imageV, this.width, this.height);
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

