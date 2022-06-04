package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ToggleButton extends AbstractButton {
    private final ItemStack itemOff = new ItemStack(ModItems.REDSTONE_TORCH_OFF.get());
    private final ItemStack itemOn = new ItemStack(Items.REDSTONE_TORCH);
    private final Consumer<ToggleButton> onPress;
    
    private final BooleanSupplier enabled;

    public ToggleButton(int x, int y, BooleanSupplier enabled, Consumer<ToggleButton> onPress) {
        super(x, y, 20, 20, null);
        
        this.enabled = enabled;
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

        int i = this.getYImage(this.isHoveredOrFocused());
        blit(poseStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        renderBg(poseStack, minecraft, mouseX, mouseY);
        
        minecraft.getItemRenderer().renderGuiItem(this.enabled.getAsBoolean() ? this.itemOn : this.itemOff, this.x + 2, this.y - 1);
    }

    @Override
    public void onPress() {
        this.onPress.accept(this);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {}
}
