package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import dev.su5ed.mffs.util.inventory.SlotInventoryInterdictionFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InterdictionMatrixMenu extends FortronMenu<InterdictionMatrixBlockEntity> {
    private int clientFortronCost;
    private int clientBiometricWarning;

    public InterdictionMatrixMenu(World world, BlockPos pos, EntityPlayer player, InventoryPlayer playerInventory) {
        super(world, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 87, 89));
        addInventorySlot(new SlotInventory(this.blockEntity.secondaryCard, 69, 89));
        addInventorySlotBox(99, 31, 4, 2, this.blockEntity.upgradeSlots);
        // 9 banned-item filter slots (display/virtual, not player-interactive)
        for (int i = 0; i < 9; i++) {
            addInventorySlot(new SlotInventoryInterdictionFilter(
                this.blockEntity.bannedItemSlots.get(i),
                9 + i * 18, 69,
                () -> this.blockEntity.getConfiscationMode().slotTintColor));
        }
        addIntDataSlot(this.blockEntity::getFortronCost, i -> this.clientFortronCost = i);
        addIntDataSlot(this.blockEntity::getBiometricWarningFlag, i -> this.clientBiometricWarning = i);
    }

    public int getClientFortronCost() {
        return this.clientFortronCost;
    }

    /** Returns true when the server has flagged that biometric-requiring modules lack an active BI. */
    public boolean getClientBiometricWarning() {
        return this.clientBiometricWarning != 0;
    }
}
