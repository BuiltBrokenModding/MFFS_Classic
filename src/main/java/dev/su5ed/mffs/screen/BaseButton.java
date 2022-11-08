package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public abstract class BaseButton extends AbstractButton {
    private final Runnable onPress;

    public BaseButton(int x, int y, int width, int height, Runnable onPress) {
        super(x, y, width, height, Component.empty());

        this.onPress = onPress;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int i = getYImage(isHoveredOrFocused());
        blit(poseStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        renderBg(poseStack, minecraft, mouseX, mouseY);

        renderFg(poseStack, minecraft, mouseX, mouseY, partialTick);
    }
    
    protected abstract void renderFg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY, float partialTick);

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {}
}
