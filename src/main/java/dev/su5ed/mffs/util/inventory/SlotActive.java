package dev.su5ed.mffs.util.inventory;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.screen.GuiColors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.function.IntSupplier;

public class SlotActive extends SlotInventory implements ColoredSlot {
    private final Activatable activatable;
    private final IntSupplier disabledColorSupplier;

    public SlotActive(InventorySlot inventorySlot, int x, int y, FortronBlockEntity be) {
        this(inventorySlot, x, y, be, () -> {
            int alpha = Math.min((int) (255 * be.getAnimation() / 4F), 0x90);
            // Combine alpha with the RGB overlay color
            return (alpha << 24) | (GuiColors.DISABLED_SLOT_OVERLAY_RGB & 0x00FFFFFF);
        });
    }

    public SlotActive(InventorySlot inventorySlot, int x, int y, Activatable activatable,
                      IntSupplier disabledColorSupplier) {
        super(inventorySlot, x, y);
        this.activatable = activatable;
        this.disabledColorSupplier = disabledColorSupplier;
    }

    public boolean isDisabled() {
        return this.activatable.isActive();
    }

    @Override
    public boolean shouldTint() {
        return isDisabled();
    }

    @Override
    public int getTintColor() {
        return this.disabledColorSupplier.getAsInt();
    }

    // isEnabled() intentionally NOT overridden: returning false causes vanilla GuiContainer
    // to skip drawItemStack() entirely, making the held item disappear. The slot is already
    // made non-interactive via canTakeStack() and isItemValid() when disabled.

    @Override    public boolean isItemValid(ItemStack stack) {
        return !isDisabled() && super.isItemValid(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return !isDisabled() && super.canTakeStack(player);
    }
}
