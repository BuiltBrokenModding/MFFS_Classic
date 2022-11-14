package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.DataSlotWrapper;
import dev.su5ed.mffs.util.SlotInventory;
import dev.su5ed.mffs.util.TransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FortronCapacitorMenu extends FortronMenu<FortronCapacitorBlockEntity> {

    public FortronCapacitorMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.FORTRON_CAPACITOR_MENU.get(), ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addUpgradeSlots();
        addDataSlot(new DataSlotWrapper(() -> this.blockEntity.getTransferMode().ordinal(), i -> this.blockEntity.setTransferMode(TransferMode.values()[i])));

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addSlot(new SlotInventory(this.blockEntity.frequencySlot, 9, 74));
            addSlot(new SlotInventory(this.blockEntity.secondaryCard, 27, 74));
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
