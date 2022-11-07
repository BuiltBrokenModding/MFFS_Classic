package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.setup.ModContainers;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FortronCapacitorMenu extends FortronMenu<FortronCapacitorBlockEntity> {

    public FortronCapacitorMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModContainers.FORTRON_CAPACITOR_MENU.get(), ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addSlot(new SlotInventory(this.blockEntity.frequencySlot, 9, 74));
            addSlot(new SlotInventory(this.blockEntity.secondaryCard, 27, 74));
        });
    }

    public int getFrequency() { // TODO Common
        return this.blockEntity.getFrequency();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
