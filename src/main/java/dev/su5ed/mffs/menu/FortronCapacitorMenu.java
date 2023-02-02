package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.DataSlotWrapper;
import dev.su5ed.mffs.util.TransferMode;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import one.util.streamex.EntryStream;

public class FortronCapacitorMenu extends FortronMenu<FortronCapacitorBlockEntity> {

    public FortronCapacitorMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.FORTRON_CAPACITOR_MENU.get(), ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addDataSlot(new DataSlotWrapper(() -> this.blockEntity.getTransferMode().ordinal(), i -> this.blockEntity.setTransferMode(TransferMode.values()[i])));

        EntryStream.of(this.blockEntity.upgradeSlots)
            .forKeyValue((i, slot) -> addSlot(new SlotInventory(slot, 154, 47 + i * 20)));
        addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 9, 74));
        addInventorySlot(new SlotInventory(this.blockEntity.secondaryCard, 27, 74));
    }
}
