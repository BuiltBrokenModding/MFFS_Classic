package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BooleanSupplier;

public class ToggleButton extends BaseButton {
    private final ItemStack itemOff = new ItemStack(ModItems.REDSTONE_TORCH_OFF.get());
    private final ItemStack itemOn = new ItemStack(Items.REDSTONE_TORCH);
    
    private final BooleanSupplier enabled;

    public ToggleButton(int x, int y, BooleanSupplier enabled, Runnable onPress) {
        super(x, y, 20, 20, onPress);
        
        this.enabled = enabled;
    }

    @Override
    public void renderFg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY, float partialTick) {
        minecraft.getItemRenderer().renderGuiItem(this.enabled.getAsBoolean() ? this.itemOn : this.itemOff, this.x + 2, this.y - 1);
    }
}
