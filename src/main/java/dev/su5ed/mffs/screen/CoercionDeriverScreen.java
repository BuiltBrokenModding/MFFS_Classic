package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.container.CoercionDeriverContainer;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleActivationPacket;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CoercionDeriverScreen extends AbstractContainerScreen<CoercionDeriverContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/base.png");

    public CoercionDeriverScreen(CoercionDeriverContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        
        this.height = this.imageHeight = 217;
    }

    @Override
    protected void init() {
        super.init();
        
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        addRenderableWidget(new ToggleButton(this.width / 2 - 82, this.height / 2 - 104, this.menu.blockEntity::isActive,
            button -> Network.INSTANCE.sendToServer(new ToggleActivationPacket(this.menu.blockEntity.getBlockPos(), !this.menu.blockEntity.isActive()))
        ));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int pMouseX, int pMouseY) {
        this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
