package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BooleanSupplier;

public class IconToggleButton extends AbstractButton {
    private final Screen screen;
    private final ResourceLocation image;
    private final int imageU;
    private final int imageV;
    private final BooleanSupplier value;
    private final BooleanConsumer onPress;

    public IconToggleButton(Screen screen, int x, int y, int width, int height, Component tooltip, int imageU, int imageV, BooleanSupplier value, BooleanConsumer onPress) {
        this(screen, x, y, width, height, tooltip, IconCycleButton.GUI_BUTTONS, imageU, imageV, value, onPress);
    }

    public IconToggleButton(Screen screen, int x, int y, int width, int height, Component tooltip, ResourceLocation image, int imageU, int imageV, BooleanSupplier value, BooleanConsumer onPress) {
        super(x, y, width, height, tooltip);

        this.screen = screen;
        this.image = image;
        this.imageU = imageU;
        this.imageV = imageV;
        this.value = value;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.value.getAsBoolean()) {
            guiGraphics.setColor(0.6F, 0.6F, 0.6F, this.alpha);
        }
        else if (isHoveredOrFocused()) {
            guiGraphics.setColor(0.85F, 0.85F, 0.85F, this.alpha);
        }
        else {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        guiGraphics.blit(this.image, getX(), getY(), this.imageU, this.imageV, this.width, this.height);
        guiGraphics.blit(this.image, getX() + this.width / 2, getY(), 200 - this.width / 2, this.imageV, this.width / 2, this.height);
        
        if (isHoveredOrFocused()) {
            guiGraphics.renderComponentTooltip(this.screen.getMinecraft().font, List.of(getMessage()), mouseX, mouseY);
        }
    }

    @Override
    public void onPress() {
        this.onPress.accept(this.value.getAsBoolean());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
