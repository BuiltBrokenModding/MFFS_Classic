package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.DoubleSupplier;

public class FortronChargeWidget extends AbstractWidget {
    public static final ResourceLocation COMPONENTS = new ResourceLocation(MFFSMod.MODID, "textures/gui/components.png");
    
    private final DoubleSupplier scale;

    public FortronChargeWidget(int x, int y, int width, int height, Component message, DoubleSupplier scale) {
        super(x, y, width, height, message);
        
        this.scale = scale;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, COMPONENTS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        double scale = this.scale.getAsDouble();
        if (scale > 0) {
            blit(poseStack, this.x, this.y, 54, 11, (int) (scale * this.width), this.height);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        
    }
}
