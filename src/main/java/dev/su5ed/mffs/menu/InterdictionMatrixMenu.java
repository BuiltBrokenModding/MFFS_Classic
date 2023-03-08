package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import dev.su5ed.mffs.util.inventory.SlotInventoryInterdictionFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class InterdictionMatrixMenu extends FortronMenu<InterdictionMatrixBlockEntity> {
    private int clientFortronCost;

    public InterdictionMatrixMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.INTERDICTION_MATRIX_MENU.get(), ModObjects.INTERDICTION_MATRIX_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 87, 89));
        addInventorySlot(new SlotInventory(this.blockEntity.secondaryCard, 69, 89));
        addInventorySlotBox(99, 31, 4, 2, this.blockEntity.upgradeSlots);
        addInventorySlotRange(0, 9, 69, 9, 18, (idx, slotX, slotY) -> new SlotInventoryInterdictionFilter(this.blockEntity.bannedItemSlots.get(idx), slotX, slotY, () -> this.blockEntity.getConfiscationMode().slotTintColor));
        addIntDataSlot(this.blockEntity::getFortronCost, i -> this.clientFortronCost = i);
    }

    public int getClientFortronCost() {
        return this.clientFortronCost;
    }
}
