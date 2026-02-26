package dev.su5ed.mffs.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.List;
import java.util.function.BooleanSupplier;

public class IconToggleButton extends AbstractButton {
    private final Screen screen;
    private final Identifier image;
    private final int imageU;
    private final int imageV;
    private final BooleanSupplier value;
    private final BooleanConsumer onPress;

    public IconToggleButton(Screen screen, int x, int y, int width, int height, Component tooltip, int imageU, int imageV, BooleanSupplier value, BooleanConsumer onPress) {
        this(screen, x, y, width, height, tooltip, IconCycleButton.GUI_BUTTONS, imageU, imageV, value, onPress);
    }

    public IconToggleButton(Screen screen, int x, int y, int width, int height, Component tooltip, Identifier image, int imageU, int imageV, BooleanSupplier value, BooleanConsumer onPress) {
        super(x, y, width, height, tooltip);

        this.screen = screen;
        this.image = image;
        this.imageU = imageU;
        this.imageV = imageV;
        this.value = value;
        this.onPress = onPress;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPress.accept(this.value.getAsBoolean());
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int color;
        if (this.value.getAsBoolean()) {
            color = ARGB.colorFromFloat(this.alpha, 0.6F, 0.6F, 0.6F);
        } else if (isHoveredOrFocused()) {
            color = ARGB.colorFromFloat(this.alpha, 0.85F, 0.85F, 0.85F);
        } else {
            color = ARGB.colorFromFloat(this.alpha, 1.0F, 1.0F, 1.0F);
        }
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.image, getX(), getY(), this.imageU, this.imageV, this.width, this.height, 256, 256, color);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.image, getX() + this.width / 2, getY(), 200 - this.width / 2, this.imageV, this.width / 2, this.height, 256, 256, color);

        if (isHoveredOrFocused()) {
            ClientTooltipComponent tooltip = ClientTooltipComponent.create(getMessage().getVisualOrderText());
            guiGraphics.renderTooltip(this.screen.getMinecraft().font, List.of(tooltip), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
