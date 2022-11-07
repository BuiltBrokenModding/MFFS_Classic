package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private final ResourceLocation background;

    public BaseScreen(T menu, Inventory playerInventory, Component title, ResourceLocation background) {
        super(menu, playerInventory, title);
        
        this.background = background;
        this.height = this.imageHeight = 217;
    }

    @Override
    protected void init() {
        super.init();
        
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public final void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderFg(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, this.background);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int pMouseX, int pMouseY) {
        this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, GuiColors.DARK_GREY);
    }

    public void renderFg(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {}
}
