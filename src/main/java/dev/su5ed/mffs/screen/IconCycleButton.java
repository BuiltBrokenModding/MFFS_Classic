package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IconCycleButton<T extends Enum<T>> extends AbstractButton {
    private final ResourceLocation image;
    private final int imageU;
    private final int imageV;
    private final int yStep;
    private final Supplier<T> value;
    private final Consumer<T> onPress;

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
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.image);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int vOffset = this.imageV + this.value.get().ordinal() * this.yStep;
        blit(poseStack, this.x, this.y, this.imageU, vOffset, this.width, this.height);
        blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, vOffset, this.width / 2, this.height);
        renderBg(poseStack, minecraft, mouseX, mouseY);
    }

    @Override
    public void onPress() {
        this.onPress.accept(this.value.get());
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
