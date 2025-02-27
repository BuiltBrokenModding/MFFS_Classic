package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public abstract class BaseButton extends AbstractButton {
    private final Runnable onPress;

    public BaseButton(int x, int y, int width, int height, Runnable onPress) {
        super(x, y, width, height, Component.empty());

        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        ResourceLocation widgetsLocation = SPRITES.get(this.active, this.isHovered());
        guiGraphics.blitSprite(RenderType::guiTextured, widgetsLocation, getX(), getY(), this.width, this.height, ARGB.white(this.alpha));

        renderFg(guiGraphics, minecraft, mouseX, mouseY, partialTick);
    }

    protected abstract void renderFg(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY, float partialTick);

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
