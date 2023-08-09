package dev.su5ed.mffs.util.inventory;

import dev.su5ed.mffs.util.TooltipSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlotInventory extends Slot implements TooltipSlot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);

    private final InventorySlot inventorySlot;
    @Nullable
    private final Component tooltip;

    public SlotInventory(InventorySlot inventorySlot, int x, int y) {
        this(inventorySlot, x, y, null);
    }

    public SlotInventory(InventorySlot inventorySlot, int x, int y, @Nullable Component tooltip) {
        super(EMPTY_INVENTORY, -1, x, y);

        this.inventorySlot = inventorySlot;
        this.tooltip = tooltip;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.inventorySlot.onChanged(true);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return !stack.isEmpty() && this.inventorySlot.canInsert(stack);
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        return this.inventorySlot.getItem();
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        this.inventorySlot.setItem(stack);
        setChanged();
    }

    @Override
    public void initialize(ItemStack stack) {
        this.inventorySlot.setItem(stack);
        setChanged();
    }

    @Override
    public void onQuickCraft(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {}

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 64;
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.inventorySlot.canExtract();
    }

    @Override
    @NotNull
    public ItemStack remove(int amount) {
        return this.inventorySlot.extract(amount, false);
    }

    @Override
    public List<Component> getTooltips() {
        return this.tooltip != null ? List.of(this.tooltip) : List.of();
    }
}
