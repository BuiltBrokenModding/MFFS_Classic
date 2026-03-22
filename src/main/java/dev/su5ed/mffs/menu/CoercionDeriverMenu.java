package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity.EnergyMode;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import one.util.streamex.EntryStream;

public class CoercionDeriverMenu extends FortronMenu<CoercionDeriverBlockEntity> {

    public CoercionDeriverMenu(World world, BlockPos pos, EntityPlayer player, InventoryPlayer playerInventory) {
        super(world, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addDataSlot(this.blockEntity::getProcessTime, this.blockEntity::setProcessTime);
        addDataSlot(() -> this.blockEntity.getEnergyMode().ordinal(),
            i -> this.blockEntity.setEnergyMode(EnergyMode.values()[i]));
        addIntDataSlot(this.blockEntity.energy::getAmountAsInt, this.blockEntity.energy::set);
        addIntDataSlot(() -> this.blockEntity.fortronProducedLastTick,
            v -> this.blockEntity.fortronProducedLastTick = v);

        EntryStream.of(this.blockEntity.upgradeSlots)
            .forKeyValue((i, slot) -> addInventorySlot(new SlotInventory(slot, 154, 47 + i * 20)));
        addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 9, 41));
        addInventorySlot(new SlotInventory(this.blockEntity.batterySlot, 9, 83));
        addInventorySlot(new SlotInventory(this.blockEntity.fuelSlot, 29, 83));
    }
}
