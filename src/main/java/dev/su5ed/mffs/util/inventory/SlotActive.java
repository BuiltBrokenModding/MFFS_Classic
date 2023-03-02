package dev.su5ed.mffs.util.inventory;

import dev.su5ed.mffs.api.Activatable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotActive extends SlotInventory {
    private final Activatable activatable;

    public SlotActive(InventorySlot inventorySlot, int x, int y, Activatable activatable) {
        super(inventorySlot, x, y);

        this.activatable = activatable;
    }

    public boolean isDisabled() {
        return this.activatable.isActive();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return !this.activatable.isActive() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return !this.activatable.isActive() && super.mayPickup(player);
    }
}
