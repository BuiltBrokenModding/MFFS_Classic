package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class CoercionDeriverMenu extends FortronMenu<CoercionDeriverBlockEntity> {

    public CoercionDeriverMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.COERCION_DERIVER_MENU.get(), ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addUpgradeSlots();

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addSlot(new SlotInventory(this.blockEntity.frequencySlot, 9, 41));

            addSlot(new SlotInventory(this.blockEntity.batterySlot, 9, 83));
            addSlot(new SlotInventory(this.blockEntity.fuelSlot, 29, 83));
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // TODO
    }
}
