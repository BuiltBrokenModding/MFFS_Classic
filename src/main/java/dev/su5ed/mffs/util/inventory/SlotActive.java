package dev.su5ed.mffs.util.inventory;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.screen.GuiColors;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;

public class SlotActive extends SlotInventory implements ColoredSlot {
    private final Activatable activatable;
    private final IntSupplier disabledColorSupplier;

    public SlotActive(InventorySlot inventorySlot, int x, int y, FortronBlockEntity be) {
        this(inventorySlot, x, y, be, () -> {
            int alpha = Math.min((int) (255 * be.getAnimation() / 4F), 0x90);
            return ARGB.color(alpha, GuiColors.DISABLED_SLOT_OVERLAY_RGB);
        });
    }

    public SlotActive(InventorySlot inventorySlot, int x, int y, Activatable activatable, IntSupplier disabledColorSupplier) {
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

    @Override
    public boolean tintItems() {
        return true;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return !isDisabled() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return !isDisabled() && super.mayPickup(player);
    }
}
