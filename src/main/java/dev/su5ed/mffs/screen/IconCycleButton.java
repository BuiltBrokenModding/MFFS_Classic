package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IconCycleButton<T extends Enum<T>> extends AbstractButton {
    public static final ResourceLocation GUI_BUTTONS = MFFSMod.location("textures/gui/buttons.png");

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
        super(x, y, width, height, Component.empty());

        this.image = image;
        this.imageU = imageU;
        this.imageV = imageV;
        this.yStep = yStep;
        this.value = value;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int color = this.isHovered ? ARGB.colorFromFloat(this.alpha, 0.85F, 0.85F, 0.85F) : ARGB.white(this.alpha);
        int vOffset = this.imageV + this.value.get().ordinal() * this.yStep;
        guiGraphics.blit(RenderType::guiTextured, this.image, getX(), getY(), this.imageU, vOffset, this.width, this.height, 256, 256, color);
        guiGraphics.blit(RenderType::guiTextured, this.image, getX() + this.width / 2, getY(), 200 - this.width / 2, vOffset, this.width / 2, this.height, 256, 256, color);
    }

    @Override
    public void onPress() {
        this.onPress.accept(this.value.get());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
