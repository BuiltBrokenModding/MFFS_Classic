package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity.EnergyMode;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import one.util.streamex.EntryStream;

public class CoercionDeriverMenu extends FortronMenu<CoercionDeriverBlockEntity> {

    public CoercionDeriverMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.COERCION_DERIVER_MENU.get(), ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addDataSlot(this.blockEntity::getProcessTime, this.blockEntity::setProcessTime);
        addDataSlot(() -> this.blockEntity.getEnergyMode().ordinal(), i -> this.blockEntity.setEnergyMode(EnergyMode.values()[i]));
        addIntDataSlot(this.blockEntity.energy::getEnergyStored, this.blockEntity.energy::setEnergy);
        addIntDataSlot(() -> this.blockEntity.fortronProducedLastTick, v -> this.blockEntity.fortronProducedLastTick = v);

        EntryStream.of(this.blockEntity.upgradeSlots)
            .forKeyValue((i, slot) -> addInventorySlot(new SlotInventory(slot, 154, 47 + i * 20)));
        addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 9, 41));
        addInventorySlot(new SlotInventory(this.blockEntity.batterySlot, 9, 83));
        addInventorySlot(new SlotInventory(this.blockEntity.fuelSlot, 29, 83));
    }
}
