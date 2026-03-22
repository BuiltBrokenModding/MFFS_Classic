package dev.su5ed.mffs.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.BooleanSupplier;

public class ToggleButton extends BaseButton {
    // 1.12.2 Backport: Blocks.UNLIT_REDSTONE_TORCH has no item form, so use
    // Items.REDSTONE as the off-state icon instead.
    private final ItemStack itemOff = new ItemStack(Items.REDSTONE);
    private final ItemStack itemOn = new ItemStack(Item.getItemFromBlock(Blocks.REDSTONE_TORCH));

    private final BooleanSupplier enabled;

    public ToggleButton(int x, int y, BooleanSupplier enabled, Runnable onPress) {
        super(x, y, 20, 20, onPress);
        this.enabled = enabled;
    }

    @Override
    public void renderFg(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        ItemStack stack = this.enabled.getAsBoolean() ? this.itemOn : this.itemOff;
        mc.getRenderItem().renderItemIntoGUI(stack, this.x + 2, this.y - 1);
    }
}
