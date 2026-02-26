package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.function.DoubleSupplier;

public class FortronChargeWidget extends AbstractWidget {
    public static final Identifier COMPONENTS = MFFSMod.location("textures/gui/components.png");

    private final DoubleSupplier scale;

    public FortronChargeWidget(int x, int y, int width, int height, Component message, DoubleSupplier scale) {
        super(x, y, width, height, message);

        this.scale = scale;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        double scale = this.scale.getAsDouble();
        if (scale > 0) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, COMPONENTS, getX(), getY(), 54, 11, (int) (scale * this.width), this.height, 256, 256, ARGB.white(this.alpha));
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
