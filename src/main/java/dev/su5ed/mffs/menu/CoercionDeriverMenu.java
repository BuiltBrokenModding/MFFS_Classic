package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModContainers;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CoercionDeriverMenu extends FortronMenu<CoercionDeriverBlockEntity> {

    public CoercionDeriverMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModContainers.COERCION_DERIVER_MENU.get(), ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addSlot(new SlotItemHandler(handler, 0, 9, 41));

            addSlot(new SlotItemHandler(handler, 1, 9, 83));
            addSlot(new SlotItemHandler(handler, 2, 29, 83));

//            addSlot(new SlotItemHandler(handler, 3, 154, 47));
//            addSlot(new SlotItemHandler(handler, 4, 154, 67));
//            addSlot(new SlotItemHandler(handler, 5, 154, 87));
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // TODO
    }
}
