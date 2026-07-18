package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public void extractForeground(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, int mouseX, int mouseY, float partialTick) {
        guiGraphics.item(this.enabled.getAsBoolean() ? this.itemOn : this.itemOff, getX() + 2, getY() - 1, 0);
    }
}
