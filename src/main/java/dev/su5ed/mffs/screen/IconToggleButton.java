package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
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
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.image);
        if (this.value.getAsBoolean()) {
            RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, this.alpha);
        }
        else if (isHoveredOrFocused()) {
            RenderSystem.setShaderColor(0.85F, 0.85F, 0.85F, this.alpha);
        }
        else {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        blit(poseStack, this.x, this.y, this.imageU, this.imageV, this.width, this.height);
        blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, this.imageV, this.width / 2, this.height);
        renderBg(poseStack, minecraft, mouseX, mouseY);
        
        if (isHoveredOrFocused()) {
            renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
        this.screen.renderComponentTooltip(poseStack, List.of(getMessage()), mouseX, mouseY);
    }

    @Override
    public void onPress() {
        this.onPress.accept(this.value.getAsBoolean());
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
